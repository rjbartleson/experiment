package core;


public class Counter {
	int cnt;
	Counter(int cnt) {
		this.cnt = cnt;
	}
	void decr() { cnt--; }
	void incr() { cnt++; }

	@Override
	public String toString() {
		return "Counter [cnt=" + cnt + "]";
	}
}
