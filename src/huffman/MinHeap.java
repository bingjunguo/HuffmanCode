package huffman;

import java.util.List;

/**
 * @author lucio.yz E-mail:lucio.yang@qq.com 
 * @date 2016年10月25日 上午10:11:55
 * @version 1.0
*/
public class MinHeap {
	private int[] heap;//保存结点的位置
	private List<HuffNode<Character>> nodes;
	private int n;//堆的元素数目
	public MinHeap(List<HuffNode<Character>> nodes){
		this.heap=new int[2*nodes.size()];//节点数目是叶子节点的2倍
		this.nodes=nodes;
		for( int i=1;i<=nodes.size();i++ ){
			heap[i]=i-1;//保存结点的位置
		}
		this.n=nodes.size();
		//构建堆
		buildMinHeap();
	}
	public MinHeap(){
		
	}
	/**
	 * 构建堆
	 */
	private void buildMinHeap(){
		for( int i=n/2;i>0;i-- ){//从下向上反复调整堆
			adjustDown(i);
		}
	}
	/**
	 * 从k开始向下调整堆
	 * @param k
	 */
	private void adjustDown(int k){
		heap[0]=heap[k];
		for( int i=2*k;i<=n;i*=2 ){//向下筛选
			if( i<n && nodes.get(heap[i]).freq>nodes.get(heap[i+1]).freq )//找到较小的子节点的坐标
				i++;
			if( nodes.get(heap[0]).freq<=nodes.get(heap[i]).freq )	
				break;//双亲结点比小的儿子还小，筛选结束
			else{//交换位置
				heap[k]=heap[i];
				k=i;//继续向下筛选
			}
		}
		heap[k]=heap[0];
	}
	/**
	 * 从k开始向上调整堆
	 * @param k
	 */
	private void adjustUp(int k){
		heap[0]=heap[k];
		int i=k/2;//父亲结点
		while( i>0 && nodes.get(heap[i]).freq>nodes.get(heap[0]).freq ){
			heap[k]=heap[i];//双亲结点下调
			k=i;
			i=k/2;//继续向上调整
		}
		heap[k]=heap[0];
	}
	/*
	 * 返回freq最小值在nodes数组中的位置
	 */
	public int extractMin(){
		if( n==0 )
			return -1;
		int po=heap[1];
		heap[1]=heap[n--];
		if( n>1 )//元素至少有两个时，才有调整的必要
			adjustDown(1);
		return po;
	}
	/**
	 * 在heap和nodes中同时插入新节点，并对堆进行调整
	 * @param node
	 */
	public void insert(HuffNode<Character> node){
		nodes.add(node);
		heap[++n]=nodes.size()-1;//新元素加到堆的末尾
		adjustUp(n);//从末尾开始向上调整堆
	}
	
}
