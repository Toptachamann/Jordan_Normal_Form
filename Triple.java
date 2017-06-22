public class Triple<L, C, R> {
	private L l;
	private C c;
	private R r;

	public Triple() {
	}

	public Triple(L l, C c, R r) {
		this.l = l;
		this.c = c;
		this.r = r;
	}

	public L left() {
		return l;
	}

	public C center() {
		return c;
	}

	public R right() {
		return r;
	}

	public void setL(L l) {
		this.l = l;
	}

	public void setC(C c) {
		this.c = c;
	}

	public void setR(R r) {
		this.r = r;
	}
	public String toString(){
		String res = l.toString() + ", " + c.toString() + ", " + r.toString();
		return res;
	}
}
