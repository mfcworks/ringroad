package ringroad;

/**
 * 交差点クラスのためのインターフェース(実装上は抽象クラス)
 *
 */
public abstract class Intersection {

	/**
	 * この交差点のX座標
	 */
	public final int thisX;

	/**
	 * この交差点のY座標
	 */
	public final int thisY;


	/**
	 * コンストラクタ
	 */
	public Intersection(int thisX, int thisY) {
		this.thisX = thisX;
		this.thisY = thisY;
	}

	public abstract int numCarsByPosition(int isec, int step);

	public abstract Intersection neighbor(int isec);

	// 交差点番号isecに接続されている道路サイトの長さを返す
	public abstract int lengthAt(int isec);

	// 内部アップデートを行なう
	public abstract int updateRoadSites();

	// 交差点から出て行くアップデート
	public abstract int updateExit();

	public abstract void connect(Intersection is0, Intersection is1, Intersection is2, Intersection is3);

	public abstract int update();

	// 車の発生を試みる
	public abstract boolean trySpawn(int isec, int step);

	// 指定された交差点番号における道路の出口から車を移動させる
	public abstract Car[] moveFromRoad(int isec, int n);

}
