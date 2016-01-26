package ringroad;

import java.util.Random;

/**
 * 放射環状道路のモデルを表すクラス
 *
 */
public class Field {

	// 交差点の2次元配列
	public Intersection[][] intersections;
	public int rc;
	public int dY;

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

		this.rc = rc;
		this.dY = dY;

		intersections = new Intersection[numX][numY];

		// 交差点オブジェクトの生成
		for (int x = 0; x < numX; x++) {
			for (int y = 0; y < numY; y++) {
				// 交差点番号0,2方向は、同心円上の環状道路部なので同じ長さ。
				// 交差点番号1,3方向は、放射道路なので同じ長さ==dY。
				// ただし、最内側と最外側は片方の放射道路を持たない。その場合は0を与える。
				int n02 = (int) Math.round((rc + (dY * y)) * 2 * Math.PI / numX);
				int n1 = (y == 0 ? 0 : dY);
				int n3 = (y == numY-1 ? 0 : dY);

				// XXX: とりあえず全て1車線道路。
				intersections[x][y] = new Roundabout(n02, n1, n02, n3, 1, 1, 1, 1);
				intersections[x][y].setPosition(x, y);
			}
		}

		// 隣接する交差点のリンク
		for (int x = 0; x < numX; x++) {
			for (int y = 0; y < numY; y++) {
				intersections[x][y].connect(
						(x == 0 ? intersections[numY-1][y] : intersections[x-1][y]),
						(y == 0 ? null : intersections[x][y-1]),
						(x == numX-1 ? intersections[0][y] : intersections[x+1][y]),
						(y == numY-1 ? null : intersections[x][y+1]));
			}
		}
	}


	/**
	 * 初期状態を設定する。ランダムな位置に車を配置する
	 *
	 * @param n 発生させる車の台数
	 */
	public void initialize(int n) {
		Random random = new Random();

		int nx = intersections.length;
		int ny = intersections[0].length;

		for (int i = 0; i < n; i++) {
			int rx, ry, ri, rs;
			boolean flag = false;
			do {
				rx = random.nextInt(nx);
				ry = random.nextInt(ny);
				ri = random.nextInt(4);
				int ni = intersections[nx][ny].lengthAt(ri);
				if (ni == 0) continue;
				rs = random.nextInt(ni);
				flag = intersections[rx][ry].trySpawn(ri, rs);
			} while(!flag);
		}
	}


	/**
	 * 系を1ステップ更新する。
	 */
	public int update() {
		// TODO: 未実装
		return -1;
	}
}