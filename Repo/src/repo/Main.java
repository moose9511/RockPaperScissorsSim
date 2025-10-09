package repo;

public class Main {
	public static class resetObserver {
		public void onReset() {
			GUI g = new GUI(new resetObserver());
		}
	}
	public static void main(String[] args) {
		GUI g = new GUI(new resetObserver());
	}

}
