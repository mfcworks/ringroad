package ringroad;

import java.awt.Color;

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
	 * 道路サイトの配列
	 */
	protected Road[] roads;

	/**
	 * 隣接する交差点の配列
	 * 交差点番号 i から伸びる道路サイトが接続する交差点を neighbors[i] に格納する。
	 */
	protected Intersection[] neighbors;


	/**
	 * コンストラクタ
	 */
	public Intersection(int thisX, int thisY) {
		this.thisX = thisX;
		this.thisY = thisY;
	}


	/**
	 * 指定された交差点番号の交差点に隣接する交差点を取得する
	 */
	public Intersection neighbor(int isec) {
		return neighbors[isec];
	}

	/**
	 * 隣接する交差点を設定する
	 * @param is0 この交差点の交差点番号0から伸びる道路サイトの先に接続する交差点
	 * @param is1 この交差点の交差点番号1から伸びる道路サイトの先に接続する交差点
	 * @param is2 この交差点の交差点番号2から伸びる道路サイトの先に接続する交差点
	 * @param is3 この交差点の交差点番号3から伸びる道路サイトの先に接続する交差点
	 */
	public void connect(Intersection is0, Intersection is1, Intersection is2, Intersection is3) {
		neighbors = new Intersection[] {is0, is1, is2, is3}; // 順番通りに格納
	}

	/**
	// 交差点番号isecに接続されている、交差点を含まないサイト数を返す。
	 */
	public int lengthAt(int isec) {
		if (roads[isec] == null) {
			return 0;
		} else {
			return roads[isec].length;
		}
	}

	public abstract int numCarsByPosition(int isec, int step);


	// 内部アップデートを行なう
	public abstract int updateRoadSites();

	// 交差点から出て行くアップデート
	public abstract int updateExit();


	public abstract int updateIntersection();

	public abstract int updateEnter();

	// 車の発生を試みる
	public abstract boolean trySpawn(int isec, int step);

	// 車の消滅を行なう。
	public abstract int tryDespawn();

	// 指定された交差点番号における道路の出口から車を移動させる
	public abstract Car[] moveFromRoad(int isec, int n);

	public abstract int getCarOut(int isec, int step);

	public abstract Color getColor(int isec, int step);

}
