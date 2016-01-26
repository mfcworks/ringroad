package ringroad;

/**
 * 交差点クラスのためのインターフェース
 *
 */
public interface Intersection {

	public int x();
	public int y();

	public Intersection[] neighbors();

	// 交差点番号isecに接続されている道路サイトの長さを返す
	public int lengthAt(int isec);

	public void setPosition(int x, int y);

	public void connect(Intersection is0, Intersection is1, Intersection is2, Intersection is3);

	public int update();

	// 車の発生を試みる
	public boolean trySpawn(int isec, int step);

	// 指定された交差点番号における道路の出口から車を移動させる
	public Car[] moveFromRoad(int isec, int n);

}
