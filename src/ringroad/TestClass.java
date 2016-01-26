package ringroad;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Spliterator;

public class TestClass {

	public static void main(String[] args) {
		LinkedList<Integer> list = new LinkedList<Integer>();

		list.add(10);
		list.add(20);
		list.add(30);

		ListIterator<Integer> lit = list.listIterator(1);

		while (lit.hasNext()) {
			System.out.println(lit.next());
		}

		Iterator<Integer> it = list.iterator();

		while (it.hasNext()) {
			System.out.println(it.next());
		}
		System.out.println();

		Spliterator<Integer> spl = list.spliterator();
		spl.forEachRemaining(x -> {
			int y = x * 2;
			System.out.println(y);
		});
	}

}
