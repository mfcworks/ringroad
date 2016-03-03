package ringroad;


public class TestClass {

	public static void main(String[] args) {
		// 単体のテスト
		int rc = 10; //中心半径分のサイト数
		int x = 10; // 放射道路の本数
		int[] ys = {3,2,1,1};
		int dy = 5; // 放射道路の間隔
		Field field = new GradualField(rc, x, ys, dy);
		field.setSpawnProbability(5.0);
		FieldView view = new FieldView(450);

		for (int i = 0;; i++) {
			field.update();
			view.draw(field);
			double d = field.getDensity();
			System.out.println("step: " + i + ", d: " + d);
			if (d >= 0.6) break;
		}
		try {
			Thread.sleep(1000*100);
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}
