package jcifs;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/9/6.
 * 联系方式: zmx_final@163.com
 */

public class NetBios {
    private static final int PORT = 137;
    private byte[] buffer = new byte[1024];
    private static final byte[] queryByte;
    private String name = null;
    private String workspace = null;
    private String mac = null;

    static {
        // 询问包结构:
        // Transaction ID 两字节（16位） 0x00 0x00
        // Flags 两字节（16位） 0x00 0x10
        // Questions 两字节（16位） 0x00 0x01
        // AnswerRRs 两字节（16位） 0x00 0x00
        // AuthorityRRs 两字节（16位） 0x00 0x00
        // AdditionalRRs 两字节（16位） 0x00 0x00
        // Name:array [1..34] 0x20 0x43 0x4B 0x41(30个) 0x00 ;
        // Type:NBSTAT 两字节 0x00 0x21
        // Class:INET 两字节（16位）0x00 0x01
        queryByte = new byte[50];
        queryByte[0] = 0x00;
        queryByte[1] = 0x00;
        queryByte[2] = 0x00;
        queryByte[3] = 0x10;
        queryByte[4] = 0x00;
        queryByte[5] = 0x01;
        queryByte[6] = 0x00;
        queryByte[7] = 0x00;
        queryByte[8] = 0x00;
        queryByte[9] = 0x00;
        queryByte[10] = 0x00;
        queryByte[11] = 0x00;
        queryByte[12] = 0x20;
        queryByte[13] = 0x43;
        queryByte[14] = 0x4B;

        for (int i = 15; i < 45; i++) {
            queryByte[i] = 0x41;
        }

        queryByte[45] = 0x00;
        queryByte[46] = 0x00;
        queryByte[47] = 0x21;
        queryByte[48] = 0x00;
        queryByte[49] = 0x01;
    }

    /**
     * 主机A向主机B发送“UDP－NetBIOS－NS”询问包，即向主机B的137端口，发Query包来询问主机B的NetBIOS Names信息。
     * 其次，主机B接收到“UDP－NetBIOS－NS”询问包，假设主机B正确安装了NetBIOS服务........... 而且137端口开放，则主机B会向主机A发送一个“UDP－NetBIOS－NS”应答包，即发Answer包给主机A。
     * 并利用UDP(NetBIOS Name Service)来快速获取远程主机MAC地址的方法
     *
     * @author WINDY
     */
    public void scan(String ip) {
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            DatagramPacket dp = new DatagramPacket(queryByte, queryByte.length, InetAddress.getByName(ip), PORT);
            datagramSocket.send(dp);
            dp = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(dp);
            mac = getMac(dp.getData());
            getName(dp.getData());

            datagramSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 表1 “UDP－NetBIOS－NS”应答包的结构及主要字段一览表
    // 序号 字段名 长度
    // 1 Transaction ID 两字节（16位）
    // 2 Flags 两字节（16位） 4
    // 3 Questions 两字节（16位） 6
    // 4 AnswerRRs 两字节（16位） 8
    // 5 AuthorityRRs 两字节（16位） 10
    // 6 AdditionalRRs 两字节（16位） 12
    // 7 Name<Workstation/Redirector> 34字节（272位）  46
    // 8 Type:NBSTAT 两字节（16位） 48
    // 9 Class:INET 两字节（16位） 50
    // 10 Time To Live 四字节（32位） 54
    // 11 Length 两字节（16位） 56
    // 12 Number of name 一个字节（8位）
    // NetBIOS Name Info 18×Number Of Name字节
    // Unit ID 6字节（48位
    private String getMac(byte[] bytes) throws Exception {
        // 获取计算机名
        int i = bytes[56] * 18 + 56;
        String s;
        StringBuilder sb = new StringBuilder(17);
        // 先从第56字节位置，读出Number Of Names（NetBIOS名字的个数，其中每个NetBIOS Names Info部分占18个字节）
        // 然后可计算出“Unit ID”字段的位置＝56＋Number Of Names×18，最后从该位置起连续读取6个字节，就是目的主机的MAC地址。

        for (int j = 1; j < 7; j++) {
            s = Integer.toHexString(0xFF & bytes[i + j]);
            if (s.length() < 2) {
                sb.append(0);
            }
            sb.append(s.toUpperCase());
            if (j < 6) sb.append(':');
        }
        return sb.toString();
    }

    // 表1 “UDP－NetBIOS－NS”应答包的结构及主要字段一览表
    // 序号 字段名 长度
    // 1 Transaction ID 两字节（16位）
    // 2 Flags 两字节（16位） 4
    // 3 Questions 两字节（16位） 6
    // 4 AnswerRRs 两字节（16位） 8
    // 5 AuthorityRRs 两字节（16位） 10
    // 6 AdditionalRRs 两字节（16位） 12
    // 7 Name<Workstation/Redirector> 34字节（272位）  46
    // 8 Type:NBSTAT 两字节（16位） 48
    // 9 Class:INET 两字节（16位） 50
    // 10 Time To Live 四字节（32位） 54
    // 11 Length 两字节（16位） 56
    // 12 Number of name 一个字节（8位） 57
    // NetBIOS Name Info 18×Number Of Name字节
    // Unit ID 6字节（48位
    private String getName(byte[] bytes) {
        // 获取计算机名
        int i = bytes[56];
        byte[] bb = new byte[i * 18];
        // 先从第56字节位置，读出Number Of Names（NetBIOS名字的个数，其中每个NetBIOS Names Info部分占18个字节）
        // 然后可计算出“Unit ID”字段的位置＝56＋Number Of Names×18，最后从该位置起连续读取6个字节，就是目的主机的MAC地址。
        System.arraycopy(bytes, 57, bb, 0, i * 18);
        String result = new String(bb);
        char[] chars = result.toCharArray();
        int first = 0, second = 0, third = 0;

        for (int i1 = 0; i1 < chars.length; i1++) {
            char c = chars[i1];
            if (c == 0) {
                if (first == 0) {
                    first = i1;
                    continue;
                }
                if (second == 0) {
                    second = i1;
                    continue;
                }
                if (third == 0) {
                    third = i1;
                    continue;
                }
            }
            if (first * second * third > 0) break;
        }

        name = result.substring(0, first).trim();
        workspace = result.substring(second, third).trim();
//        System.out.println(name + "  " + workspace);
        return name;
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public String getWorkspace() {
        return workspace;
    }
}
