package huffman;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lucio.yz E-mail:lucio.yang@qq.com 
 * @date 2016年10月25日 下午8:37:26
 * @version 1.0
 * OutputStream的包装类，提供按位输出
*/
public class BitOutputStream {    
    private OutputStream out; //基本输出流    
    private int buffer;//输出的缓冲区    
    private int bufferPos;//缓冲区中剩余的位数    
    /**  
     * 封装OutputStream的构造方法，初始化缓冲区大小  
     * @param os  
     */    
    public BitOutputStream(OutputStream os){    
        bufferPos=0;    
        buffer=0;    
        out=os;    
    }    
    /**  
     * 写入一串的位  
     * @param val 包含有位数据的数组  
     * @throws IOException  
     */    
    public void writeBits(int []val) throws IOException{    
        int len=val.length;    
        for(int i=0;i<len;i++){    
            writeBit(val[i]);    
        }    
    }    
    /**  
     * 写入位的方法(0或1)，每8次对其进行调用就从基本流中写入一个byte  
     * @param val 当前写入的位数据  
     * @throws IOException  
     */    
    public void writeBit(int val) throws IOException{    
        buffer=setBit(buffer,bufferPos++,val);//将缓冲数据转换成位数据    
        //每读到一个byte就刷新一次    
        if(bufferPos==8)//缓冲区已满则刷新缓冲区    
            flush();    
    }    
    /**  
     * 刷新此缓冲的输出流  
     * @throws IOException  
     */    
    public void flush() throws IOException{    
        if(bufferPos==0)//如果缓冲中没有数据则不执行    
            return;    
        //将缓冲区中的数据写入    
        out.write(buffer);    
        //重置缓冲区    
        bufferPos=0;    
        buffer=0;    
    }    
    /**  
     * 关闭流的方法  
     * @throws IOException  
     */    
    public void close() throws IOException{    
        flush();    
        out.close();    
    }    
    /**  
     * 进行位数据转换的方法  
     * @param pack  
     * @param pos  
     * @param val 当前位  
     * @return  
     */    
    private int setBit(int pack,int pos,int val){    
        if(val==1)    
            //按位或运算    
            pack|=(val<<pos);    
        return pack;    
    }    
}    
