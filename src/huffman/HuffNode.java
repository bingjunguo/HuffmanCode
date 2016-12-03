package huffman;
/**
 * @author lucio.yz E-mail:lucio.yang@qq.com 
 * @date 2016年10月25日 下午1:45:30
 * @version 1.0
*/
public class HuffNode<E> {
	E symbol;
	int freq;
	int leftChild;
	int rightChild;
	int parent;
	public HuffNode(E symbol,int freq){
		this.symbol=symbol;
		this.freq=freq;
		this.leftChild=-1;
		this.rightChild=-1;
		this.parent=-1;
	}
	public HuffNode(int freq,int leftChild,int rightChild){
		this.freq=freq;
		this.leftChild=leftChild;
		this.rightChild=rightChild;
		this.parent=-1;
	}
}
