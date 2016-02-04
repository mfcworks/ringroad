package ringroad;

import java.util.Random;

/**
 * 放射環状道路のモデルを表すクラス
 *
 */
public class Field {

	/**
	 * 交差点の2次元配列
	 */
	private Intersection[][] intersections;

	/**
	 * 中心半径
	 */
	public final int rc;
	/**
	 * 放射道路の本数
	 */
	public final int numX;
	/**
	 * 環状道路の本数
	 */
	public final int numY;
	/**
	 * 放射道路の1区間の長さ
	 */
	public final int dY;
	/**
	 * 環状道路の1区間の長さ
	 */
	public final int[] dX;

	/**
	 * コンストラクタ
	 *
	 * @param rc   中心半径
	 * @param numX 放射道路の本数
	 * @param numY 環状道路の本数
	 * @param dY   環状道路の1区間の長さ
	 */
	public Field(int rc, int numX, int numY, int dY) {
		// 例によってField情報をCarに置いておく。
		Car.field = this;

		this.numX = numX;
		this.numY = numY;
		this.rc = rc;
		this.dY = dY;

		intersections = new Intersection[numX][numY];

		dX = new int[numY];

		// DEBUG
		System.out.println("Field initializing...");
		System.out.println("放射道路の区間長: " + dY);

		// 交差点オブジェクトの生成
		for (int x = 0; x < numX; x++) {
			for (int y = 0; y < numY; y++) {
				// 交差点番号0,2方向は、同心円上の環状道路部なので同じ長さ。
				// 交差点番号1,3方向は、放射道路なので同じ長さ==dY。
				// ただし、最内側と最外側は片方の放射道路を持たない。その場合は0を与える。
				int n02 = (int) Math.round((rc + (dY * y)) * 2 * Math.PI / numX);
				int n1 = (y == 0 ? 0 : dY);
				int n3 = (y == numY-1 ? 0 : dY);

				dX[y] = n02;

				// XXX: とりあえず全て1車線道路。
				intersections[x][y] = new Roundabout(x, y, n02, n1, n02, n3, 1, 1, 1, 1);

				if (x == 0) System.out.println("環状道路" + y + "の区間長: " + n02);
			}
		}

		// 隣接する交差点のリンク
		for (int x = 0; x < numX; x++) {
			for (int y = 0; y < numY; y++) {
				intersections[x][y].connect(
						(x == 0 ? intersections[numX-1][y] : intersections[x-1][y]),
						(y == 0 ? null : intersections[x][y-1]),
						(x == numX-1 ? intersections[0][y] : intersections[x+1][y]),
						(y == numY-1 ? null : intersections[x][y+1]));
			}
		}
	}


	/**
	 * 位置(x, y)の交差点を取得します。
	 */
	public Intersection getIntersection(int x, int y) {
		return intersections[x][y];
	}

	/**
	 * 交差点(x,y)の交差点番号isecの道路サイトの長さを取得する。
	 * @return 交差点サイトを除いた道路サイトのサイト数
	 */
	public int lengthAt(int x, int y, int isec) {
		return intersections[x][y].lengthAt(isec);
	}


	/**
	 * 初期状態を設定する。ランダムな位置に車を配置する
	 *
	 * @param n 発生させる車の台数
	 */
	public void initialize(int n) {
		Random random = new Random();

		for (int i = 0; i < n; i++) {
			int rx, ry, ri, rs;
			boolean flag = false;
			do {
				rx = random.nextInt(numX);
				ry = random.nextInt(numY);
				ri = random.nextInt(4);
				int ni = intersections[rx][ry].lengthAt(ri);
				if (ni == 0) continue;
				rs = random.nextInt(ni);
				flag = intersections[rx][ry].trySpawn(ri, rs);
			} while(!flag);
		}
	}


	int lastmoved = 0;
	int zeromoved = 0;

	/**
	 * 系を1ステップ更新する。
	 */
	public int update() {
		int deleted = 0;
		// Phase 0: 車が目的地に到着しているか調べて消滅させる
		for (int x = 0; x < numX; x++) {
			for (int y = 0; y < numY; y++) {
				deleted += intersections[x][y].tryDespawn();
			}
		}

		int moved = 0;
		// Phase 1: 全ての交差点について内部アップデートを行なう
		for (int x = 0; x < numX; x++) {
			for (int y = 0; y < numY; y++) {
				moved += intersections[x][y].updateRoadSites();
			}
		}
		// Phase 2: 全ての交差点について、交差点から道路サイトへ抜ける車を移動させる
		for (int x = 0; x < numX; x++) {
			for (int y = 0; y <numY; y++) {
				moved += intersections[x][y].updateExit();
			}
		}
		// Phase 3: 全ての交差点について、交差点を回る全ての車を移動させる
		for (int x = 0; x < numX; x++) {
			for (int y = 0; y < numY; y++) {
				moved += intersections[x][y].updateIntersection();
			}
		}
		// Phase 4: update enter
		for (int x = 0; x < numX; x++) {
			for (int y = 0; y < numY; y++) {
				moved += intersections[x][y].updateEnter();
			}
		}

		if (lastmoved == 0 && moved == 0) {
			zeromoved++;
		}
		lastmoved = moved;
		if (zeromoved > 5) {
			throw new RuntimeException();
		}

		return moved;
	}


	/**
	 * 描画用インターフェース：指定した位置のサイトに入っている車の台数を取得する
	 */
	public int numCarsByPosition(int x, int y, int isec, int step) {
		return intersections[x][y].numCarsByPosition(isec, step);
	}

	/**
	 * for Debug: get car dir(out intersection)
	 */
	public int getCarOut(int x, int y, int isec, int step) {
		return intersections[x][y].getCarOut(isec, step);
	}
}
