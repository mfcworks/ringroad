package ringroad;


public class TestClass {

	public static void main(String[] args) {
		// 単体のテスト
		int rc = 10; //中心半径分のサイト数
		int x = 4; // 環状道路の本数
		int y = 10; // 放射道路の本数
		int dy = 5; // 放射道路の間隔
		Field field = new Field(rc, x, y, dy);
		System.out.println("Field init ok.");
	}

}
