import java.util.Random;
import dict.*;
import graph.*;
import set.*;
import list.*;

/**
 * The Kruskal class contains the method minSpanTree(), which implements
 * Kruskal's algorithm for computing a minimum spanning tree of a graph.
 * It uses quickSort to sort the edges in the graph g by weight.
 */

public class Kruskal {

	/**
	 * minSpanTree() returns a WUGraph that represents the minimum spanning tree
	 * of the WUGraph g.  The original WUGraph g is NOT changed.
	 */
	public static WUGraph minSpanTree(WUGraph g){

		// This code calls getVertices to generate a array of all the vertices of g,
		// and then adds each vertex from getVertices to the WUGraph minSpan.
		// Then, for each vertex in the array, it calls getNeighbors. 
		// For each neighbor, an edge is added to the Queue of all edges.
		WUGraph minSpan = new WUGraph();
		Object[] allVertices = g.getVertices();
		Neighbors alpha;
		LinkedQueue allEdges =  new LinkedQueue();
		int i = 0, j = 0;
		for(i = 0;i<allVertices.length;i++){
			minSpan.addVertex(allVertices[i]);
			alpha = g.getNeighbors(allVertices[i]);
			if(alpha!=null){
				for (j = 0;j<alpha.neighborList.length;j++){
					allEdges.enqueue(new KruskalEdge(allVertices[i],alpha.neighborList[j],alpha.weightList[j]));
				}
			}
		}

		//uses quickSort from hw8. KruskalEdge implements Comparable by comparing the weights of the edges.
		Kruskal.quickSort(allEdges);

		//Need to map the vertices to a hash table in order to use DisjoinSets.
		//Each entry of the hash table stores the vertex as the key, and an index
		//between zero and the number of vertices minus one as the value.
		HashTableChained map = new HashTableChained(allVertices.length);
		for (i = 0;i<allVertices.length;i++){
			map.insert(allVertices[i],i);
		}

		//This section uses DisjointSets to create the minSpanTree, as layed
		//out in the readme.
		DisjointSets DSVertices = new DisjointSets(allVertices.length);
		Object node1, node2; //vertices from KruskalEdge
		int root1, root2; // the index of the roots in the DisjointSets Object.
		Object delta; //the KruskalEdge Object in the Queue of all edges.
		while(!allEdges.isEmpty()){
			try{
				delta = (KruskalEdge)allEdges.dequeue();
				node1 = ((KruskalEdge)delta).source;
				node2 = ((KruskalEdge)delta).drain;
				root1 =  DSVertices.find(map.find(node1).value());
				root2 =  DSVertices.find(map.find(node2).value());
				if (root1!=root2){
					// if the roots are not equals, than edge connecting through a path the vertices
					// they correspond to has not yet been added. So, an edge is added, and then 
					// the roots are unioned.
					minSpan.addEdge(node1,node2,((KruskalEdge)delta).weight);
					DSVertices.union(root1,root2);
				}
			}catch(QueueEmptyException qee){
				System.err.println(qee);
			}
		}
		return minSpan;
	}


	/**
	 * The following two methods are taken directly from hw8. Together they
	 * implement Quick Sort using Queues. the quickSort() method is used
	 * by the minSpanTree method to sort the edges by weight.
	 */

	/**
	 *  quickSort() sorts q from smallest to largest using quicksort.
	 *  @param q is a LinkedQueue of Comparable objects.
	 **/
	private static void quickSort(LinkedQueue q) {
		if(q.size()>1){
			Random generator = new Random();
			int randNumber = generator.nextInt(q.size()) + 1; //add 1 because its exclusive on the input int.
			Comparable pivot = (Comparable) q.nth(randNumber);
			try{
				LinkedQueue qSmall = new LinkedQueue();
				LinkedQueue qEquals = new LinkedQueue();
				LinkedQueue qLarge = new LinkedQueue();
				partition(q,(Comparable) q.front(),qSmall,qEquals,qLarge);
				quickSort(qSmall);
				quickSort(qLarge);
				q.append(qSmall);
				q.append(qEquals);
				q.append(qLarge);
			}catch(QueueEmptyException qee){
				System.err.println("q is empty");
			}
		}
	}

	/**
	 *  partition() partitions qIn using the pivot item.  On completion of
	 *  this method, qIn is empty, and its items have been moved to qSmall,
	 *  qEquals, and qLarge, according to their relationship to the pivot.
	 *  @param qIn is a LinkedQueue of Comparable objects.
	 *  @param pivot is a Comparable item used for partitioning.
	 *  @param qSmall is a LinkedQueue, in which all items less than pivot
	 *    will be enqueued.
	 *  @param qEquals is a LinkedQueue, in which all items equal to the pivot
	 *    will be enqueued.
	 *  @param qLarge is a LinkedQueue, in which all items greater than pivot
	 *    will be enqueued.  
	 **/   
	private static void partition(LinkedQueue qIn, Comparable pivot, LinkedQueue qSmall, 
			LinkedQueue qEquals, LinkedQueue qLarge){
		Comparable current = null;
		try{
			while(!qIn.isEmpty()){
				current = (Comparable) qIn.dequeue();
				if (current.compareTo(pivot)<0){
					qSmall.enqueue(current);
				}else if(current.compareTo(pivot)==0){
					qEquals.enqueue(current);
				}else{
					qLarge.enqueue(current);
				}
			}
		}catch(QueueEmptyException qee){
			System.err.println("qIn is empty");
		}
	}
}


/**
 * This class is used by the Kruskal class to store the input vertices,
 * which are any Object, and the weight of the edge connecting the vertices. 
 * It implements Comparable so that Kruskal can sort the list of edges
 * by just calling compareTo() in the partition method of Quick Sort.
 */

class KruskalEdge implements Comparable {

	Object source; //source vertex
	Object drain; // drain vertex
	int weight; //the weight of the edge connecting the source and drain

	KruskalEdge(Object a, Object b, int c){
		source = a;
		drain = b;
		weight = c;
	}	

	public int compareTo(Object o){
		if (this.weight<((KruskalEdge)o).weight){
			return -1;
		}else if(this.weight>((KruskalEdge)o).weight){
			return 1;
		}else{
			return 0;
		}
	}
}
