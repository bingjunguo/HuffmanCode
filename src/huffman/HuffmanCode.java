package huffman;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author lucio.yz E-mail:lucio.yang@qq.com 
 * @date 2016年10月25日 上午10:09:44
 * @version 1.0
*/
public class HuffmanCode {
	private static final int EOF=256;
	/**
	 * 统计字符频率，产生huff结点
	 * @param filePath 文件路径
	 * @return 统计的hash结果
	 */
	private Map<Character, Integer> countFreq(String filePath){
		File file=new File(filePath);
		Map<Character, Integer> map=new HashMap<Character, Integer>();
		if( file.isFile() && file.exists() ){
			FileInputStream FIS=null;
			InputStreamReader ISR=null;
			BufferedReader BR=null;
			try {
				FIS=new FileInputStream(file);
				ISR=new InputStreamReader(FIS);
				BR=new BufferedReader(ISR);
				int temp=0;
				Character ch=null;
				Integer value=null;
				//字符频率统计
				while( (temp=BR.read())!=-1){//read is safe for english
					ch=(char)temp;
					//if( Character.isWhitespace(ch) )
					//	continue;
					value=map.get(ch);
					if( value==null )
						map.put(ch, 1);
					else
						map.put(ch,value+1);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("读取文件内容出错！");
			}finally {
				try {
					FIS.close();
					ISR.close();
					BR.close();
				} catch (Exception e2) {
					e2.printStackTrace();
					System.out.println("无法关闭文件流！");
				}
			}			
		}
		else
			System.out.println("系统找不到指定的文件！");
		return map;
	}
	/**
	 * 根据字符频率生成相应的huffman结点
	 * @param map
	 * @return huffman结点列表
	 */
	private List<HuffNode<Character>> genHuffNodes(Map<Character, Integer> map){
		//统计结束，生成huff结点，写入字符和频
		List<HuffNode<Character>> nodes=new ArrayList<HuffNode<Character>>();
		Iterator<Entry<Character, Integer>> iter=map.entrySet().iterator();
		while( iter.hasNext() ){
			Entry<Character, Integer> entry=iter.next();
			HuffNode<Character> huffNode=new HuffNode<Character>(entry.getKey(), entry.getValue());
			nodes.add(huffNode);
			entry.setValue(nodes.size()-1);
		}
		return nodes;
	}
	/**
	 * 在nodes数组的基础上，建立huffman树
	 * @param nodes
	 * @param minHeap
	 */
	public int genHuffTree(List<HuffNode<Character>> nodes,MinHeap minHeap){
		int root=-1;
		while( true ){
			//优先队列中找到两个最小元素
			int po1=minHeap.extractMin();
			int po2=minHeap.extractMin();
			if( po2==-1 ){//剩余一个结点
				root=po1;
				break;
			}
			//产生新huff结点
			HuffNode<Character> newNode=new HuffNode<Character>(nodes.get(po1).freq+nodes.get(po2).freq,po1,po2);
			minHeap.insert(newNode);
			nodes.get(po1).parent=nodes.size()-1;
			nodes.get(po2).parent=nodes.size()-1;
		}
		return root;
	}
	/**
	 * 生成码表，String数组的每一项为哈夫曼树中对应项的编码
	 * @param n
	 * @param nodes
	 * @return
	 */
	private List<int[]> genCodeTable(int n,List<HuffNode<Character>> nodes){
		List<int[]> codes=new ArrayList<int[]>();
		for( int i=0;i<n;i++ ){//对所有叶子节点进行遍历
			HuffNode<Character> node=nodes.get(i);
			int parent=node.parent;
			int child=i;
			String code="";
			while( parent!=-1 ){
				HuffNode<Character> p_node=nodes.get(parent);
				if( p_node.leftChild==child )//i结点是父亲的左孩子，编码前缀位0
					code="0"+code;
				else if( p_node.rightChild==child )//i结点是父亲的左孩子，编码前缀位0
					code="1"+code;
				child=parent;
				parent=p_node.parent;
			}
			int len=code.length();
			int[] temp=new int[len];
			for( int j=0;j<len;j++ ){
				temp[j]=code.charAt(j)=='0'?0:1;
			}
			codes.add(temp);
		}
		return codes;
	}
	
	private void genCompressFile(
			int n,
			List<HuffNode<Character>> nodes,
			List<int[]> codeTable,
			String filePath,
			String outputPath,
			Map<Character, Integer> map) throws IOException{
		File inFile=new File(filePath);		
		FileInputStream FS=new FileInputStream(inFile);
		InputStreamReader ISR=new InputStreamReader(FS);
		BufferedReader BR=new BufferedReader(ISR);
		
		File outFile=new File(outputPath);
		OutputStream OS=new BufferedOutputStream(new FileOutputStream(outFile));
		DataOutputStream DOS=new DataOutputStream(OS);
		BitOutputStream BOS=new BitOutputStream(DOS);
		//向新文件写入码表，产生(n+1)*(4+1)个字节
		for( int i=0;i<n;i++ ){
			DOS.writeByte((char)nodes.get(i).symbol);
			DOS.writeInt(nodes.get(i).freq);
		}
		DOS.writeByte(0);//连续两个0表示码表结束
		DOS.writeInt(0);
		//向新文件写入原文件编码
		int temp=0;
		Character ch=null;
		int po=0;
		while( (temp=BR.read())!=-1){//遍历原文件
			ch=(char)temp;
			//if( Character.isWhitespace(ch) )
			//	continue;
			po=map.get(ch);//获得码表位置
			//输出对应编码
			BOS.writeBits(codeTable.get(po));
		}
		//BOS.writeBits(EOF);
		//关闭流
		FS.close();
		ISR.close();
		BR.close();
		
		OS.close();
		DOS.close();
		BOS.close();
	}
	/**
	 * 从压缩文件中读取码表，得到字符频率哈希表
	 * @param filePath
	 * @param DIS
	 * @return
	 * @throws IOException
	 */
	private Map<Character, Integer> getCountFreqFromFile(
			String filePath,
			DataInputStream DIS) throws IOException{
		Map<Character, Integer> map=new HashMap<Character,Integer>();
		//从压缩文件中读取码表
		byte symbol;
		int freq;
		while( true ){
			symbol=DIS.readByte();//读到的字节
			freq=DIS.readInt();//读到的频率
			if( freq==0 )//码表结束标志
				break;
			map.put((char)symbol, freq);
		}
		return map;
	}
	/**
	 * 生成解压缩文件
	 * @param root
	 * @param nodes
	 * @param BIS
	 * @param BW
	 */
	private void genDeCompressFile(int root,List<HuffNode<Character>> nodes,BitInputStream BIS,BufferedWriter BW){
		String bits="";//读取的编码
		int bit;//读取的每一位
		char decode;//解码的字符
		int node;//当前遍历的节点
		boolean flag=false;
		try {
			while((bit=BIS.readBit())!=-1){
				bits+=bit;
				node=root;
				decode='0';
				for( int i=0;i<bits.length();i++ ){
					if( bits.charAt(i)=='0' )
						node=nodes.get(node).leftChild;
					else if( bits.charAt(i)=='1' )
						node=nodes.get(node).rightChild;
					if( i==bits.length()-1 ){//编码结束
						if( nodes.get(node).leftChild==-1 && nodes.get(node).rightChild==-1 ){//到了叶子节点
							flag=true;
							decode=nodes.get(node).symbol;
							break;
						}else{//树的遍历还没到叶子节点
							flag=false;break;
						}
					}
				}
				if( flag ){//一次解码完成
					bits="";
					BW.write(decode);
					flag=false;
				}
			}
			BW.flush();
		} catch (Exception e) {
			System.out.println("fail to decompress by bit！");
			e.printStackTrace();
		}
	}
	/**
	 * 对文本进行huffman编码
	 * @param filePath
	 */
	public void encode(String filePath,String outputPath){
		System.out.println("------start to encode "+filePath);
		//统计字符频率；map保存字符频率
		Map<Character, Integer> map=countFreq(filePath);
		System.out.println("1.counting of char frequence has done!");
		//生成哈夫曼节点;map保存nodes位置
		List<HuffNode<Character>> nodes=genHuffNodes(map);
		System.out.println("2.generating of huffman nodes has done!");		
		int n_leaf=nodes.size();//叶子节点个数
		//构建小根堆
		MinHeap minHeap=new MinHeap(nodes);
		System.out.println("3.constructure of min heap has done!");
		//在nodes的基础上，构建哈夫曼树
		genHuffTree(nodes, minHeap);
		System.out.println("4.constructure of huffman tree has done!");
		//遍历哈夫曼树，产生前缀编码
		List<int[]> codeTable=genCodeTable(n_leaf,nodes);
		System.out.println("5.generating of code table has done!");
		//生成压缩文件
		try {
			genCompressFile(n_leaf, nodes, codeTable, filePath, outputPath,map);
			System.out.println("6.output of compressed file has done!");
		} catch (Exception e) {
			System.out.println("fail to output compressed file！");
			e.printStackTrace();
		}
	}
	public void decode(String filePath,String outputPath) throws IOException{
		System.out.println("------start to decode "+filePath);
		//for read
		File inFile=new File(filePath);
		InputStream IS=new BufferedInputStream(new FileInputStream(inFile));
		DataInputStream DIS=new DataInputStream(IS);
		BitInputStream BIS=new BitInputStream(IS);
		//for write
		File outFile=new File(outputPath);
		FileOutputStream FOS=new FileOutputStream(outFile);
		OutputStreamWriter OSW=new OutputStreamWriter(FOS);
		BufferedWriter BW=new BufferedWriter(OSW);
		//根据压缩文件中保存的码表，还原字符频率哈希表
		Map<Character, Integer> map;
		map=getCountFreqFromFile(filePath,DIS);
		System.out.println("1.load code table from compressed file!");
		//生成哈夫曼节点;map保存nodes位置
		List<HuffNode<Character>> nodes=genHuffNodes(map);
		System.out.println("2.generating of huffman nodes has done!");		
		//构建小根堆
		MinHeap minHeap=new MinHeap(nodes);
		System.out.println("3.constructure of min heap has done!");
		//在nodes的基础上，构建哈夫曼树
		int root=genHuffTree(nodes, minHeap);
		System.out.println("4.constructure of huffman tree has done!");
		//生成压缩文件
		genDeCompressFile(root,nodes,BIS,BW);
		System.out.println("5.output of decompress file has done!");
		IS.close();
		DIS.close();
		BIS.close();
		
		FOS.close();
		OSW.close();
		BW.close();
	}
	/** 
	 * @param args
	 */
	public static void main(String[] args) {
		HuffmanCode h=new HuffmanCode();
		//h.encode("try1.txt","try1_com.txt");
		//h.encode("try2.txt","try2_com.txt");
		h.encode("graph.txt","graph_com.txt");
		//h.encode("Aesop_Fables.txt","Aesop_Fables_com.txt");
		File oldFile=new File("graph.txt");
		File comFile=new File("graph_com.txt");
		//File oldFile=new File("Aesop_Fables.txt");
		//File comFile=new File("Aesop_Fables_com.txt");
		System.out.println("compress rate="+String.format("%.3f",(float)comFile.length()/oldFile.length()));
		try {
			//h.decode("try1_com.txt", "try1_decom.txt");			
			//h.decode("try2_com.txt", "try2_decom.txt");
			//h.decode("Aesop_Fables_com.txt", "Aesop_Fables_decom.txt");
			h.decode("graph_com.txt", "graph_decom.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
