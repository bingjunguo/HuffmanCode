package huffman;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author lucio.yz E-mail:lucio.yang@qq.com
 * @date 2016年10月26日 下午3:37:45
 * @version 1.0 InputStream的包装类，提供按位输入
 */
public class BitInputStream {
	private InputStream in;// 基本输入流
	private int buffer;// byte缓冲区
	private int bufferPos;// 表示缓冲区中有多少未被使用的空间

	/**
	 * 封装InputStream的构造方法，初始化byte缓冲区的大小
	 * 
	 * @param is
	 *            InputStream对象
	 */
	public BitInputStream(InputStream is) {
		in = is;
		bufferPos = 8;// 初始化缓冲区的剩余空间
	}

	/**
	 * 读取一位的方法，每8次对其进行调用就会从基本输入流中读出一个byte
	 * 
	 * @return 1位数据，1或者0
	 * @throws IOException
	 */
	public int readBit() throws IOException {
		// 如果缓冲区还未被使用
		if (bufferPos == 8) {
			// 输入流读取一位
			buffer = in.read();
			// 读到文件的末尾了
			if (buffer == -1)
				return -1;
			// 清空缓冲区
			bufferPos = 0;
		}
		// 扩张缓冲区
		return getBit(buffer, bufferPos++);
	}

	/**
	 * 关闭输入流
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		in.close();
	}

	/**
	 * 获取一个byte中每一位的方法
	 * 
	 * @param pack
	 * @param pos
	 * @return
	 */
	private static int getBit(int pack, int pos) {
		// 将一个字节进行8次按位与运算，得到这个字节的8位
		return (pack & (1 << pos)) != 0 ? 1 : 0;
	}
}
