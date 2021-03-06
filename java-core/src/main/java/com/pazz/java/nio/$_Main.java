package com.pazz.java.nio;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

/*
 * 一、通道（Channel）：用于源节点与目标节点的连接。在 Java NIO 中负责缓冲区中数据的传输。Channel 本身不存储数据，因此需要配合缓冲区进行传输。
 *
 * 二、通道的主要实现类
 *  java.nio.channels.Channel 接口：
 *      |--FileChannel
 *      |--SocketChannel
 *      |--ServerSocketChannel
 *      |--DatagramChannel
 *
 * 三、获取通道
 *  1. Java 针对支持通道的类提供了 getChannel() 方法
 *  本地 IO：
 *      FileInputStream/FileOutputStream
 *      RandomAccessFile
 *
 *  网络IO：
 *      Socket
 *      ServerSocket
 *      DatagramSocket
 *  2. 在 JDK 1.7 中的 NIO.2 针对各个通道提供了静态方法 open()
 *  3. 在 JDK 1.7 中的 NIO.2 的 Files 工具类的 newByteChannel()
 *
 * 四、通道之间的数据传输
 *      transferFrom()
 *      transferTo()
 *
 * 五、分散(Scatter)与聚集(Gather)
 *      分散读取（Scattering Reads）：将通道中的数据分散到多个缓冲区中
 *      聚集写入（Gathering Writes）：将多个缓冲区中的数据聚集到通道中
 *
 * 六、字符集：Charset
 *      编码：字符串 -> 字节数组
 *      解码：字节数组  -> 字符串
 */
public class $_Main {

    @Test
    public void server() throws Exception {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(8899));

        // socket 通道
        SocketChannel socketChannel = serverChannel.accept();

        // file 通道
        FileChannel fileChannel = FileChannel.open(Paths.get("F:/图片/布鲁克2copy.jpg"),
                StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);

        // byte 缓存区
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // 从该通道读取到给定缓冲区的字节序列; 读取的字节数,可能为零,如果通道已达到流出端, 则为-1
        while (socketChannel.read(buffer) != -1) {
            // 翻转这个缓冲区。 该限制设置为当前位置，然后将该位置设置为零。 如果标记被定义，则它被丢弃
            buffer.flip();
            // 从给定的缓冲区向该通道写入一个字节序列
            fileChannel.write(buffer);
            // 清除此缓冲区。 位置设置为零，限制设置为容量，标记被丢弃
            buffer.clear();
        }

        // 在不关闭通道的情况下，关闭读取连接。
        socketChannel.shutdownInput();

        buffer.put("服务端已接收~".getBytes());
        buffer.flip();
        socketChannel.write(buffer);

        fileChannel.close();
        socketChannel.close();
        serverChannel.close();
    }

    @Test
    public void serverBlocking() throws Exception {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(8899));

        serverChannel.configureBlocking(false);

        // 创建选择器
        Selector selector = Selector.open();

        // SelectionKey.OP_ACCEPT 操作集位用于 socket 接收操作
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (selector.select() > 0) {
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

            while (keyIterator.hasNext()) {
                SelectionKey selectionKey = keyIterator.next();

                // 测试此密钥的通道是否已准备好接收新的 socket 连接
                if (selectionKey.isAcceptable()) {

                    SocketChannel socketChannel = serverChannel.accept();
                    socketChannel.configureBlocking(false);
                    // SelectionKey.OP_READ 读操作的操作位
                    socketChannel.register(selector, SelectionKey.OP_READ);

                    // 测试此密钥的频道是否可以阅读
                } else if (selectionKey.isReadable()) {
                    // 返回创建此键的通道
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                    ByteBuffer buffer = ByteBuffer.allocate(1024);

                    int length;
                    while ((length = socketChannel.read(buffer)) != -1) {
                        buffer.flip();
                        System.out.println(new String(buffer.array(), 0, length));
                        buffer.clear();
                    }

                }
            }
            keyIterator.remove();
        }
    }

    @Test
    public void client() throws Exception {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(8899));

        // 需要发送的 file 通道
        FileChannel fileChannel = FileChannel.open(Paths.get("F:/图片/布鲁克2.jpg"),
                StandardOpenOption.READ);

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while (fileChannel.read(buffer) != -1) {
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
        }

        // 在不关闭通道的情况下，为写入而关闭连接
        socketChannel.shutdownOutput();

        int length = 0;
        while ((length = socketChannel.read(buffer)) != -1) {
            buffer.flip();
            System.out.println(new String(buffer.array(), 0, length));
            buffer.clear();
        }

        fileChannel.close();
        socketChannel.close();
    }

    @Test
    public void clientBlocking() throws Exception {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(8899));
        socketChannel.configureBlocking(false);

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put("客户端: send~~".getBytes());
        buffer.flip();
        socketChannel.write(buffer);
        buffer.clear();

        socketChannel.close();
    }

    public static void main(String[] args) throws Exception {
        FileChannel inChannel = FileChannel.open(Paths.get("F:/图片/布鲁克1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("F:/图片/布鲁克1-copy.jpg"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

        // 内存映射文件
//        MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
//        MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());
//
//        byte[] b = new byte[inMappedBuf.limit()];
//        inMappedBuf.get(b);
//        outMappedBuf.put(b);

//        inChannel.transferTo(0, inChannel.size(), outChannel);
        outChannel.transferFrom(inChannel, 0, inChannel.size());

        inChannel.close();
        outChannel.close();

        Charset charset = Charset.forName("utf-8");
        System.out.println(charset.newEncoder());
    }

}
