import java.util.Scanner;
import java.io.*;

public class LMatrix {
	private long[][] matrix;
	int size;
	private Scanner in;
	private PrintWriter out;

	public LMatrix() {
		initializeStreams();
	}

	public LMatrix(long arr[][]) {
		initializeStreams();
		size = arr.length;
		matrix = new long[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				matrix[i][j] = arr[i][j];
			}
		}
	}

	private void initializeStreams() {
		in = new Scanner(System.in);
		OutputStream outputStream = System.out;
		out = new PrintWriter(outputStream);
		out.println(5);
	}
	
	public String toString(){
		String res = "";
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				res += String.valueOf(matrix[i][j]) + " ";
			}
			res += '\n';
		}
		return res;
	}
	
	public void read() {
		int size = in.nextInt();
		matrix = new long[size][size];
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				matrix[i][j] = in.nextLong();
			}
		}
	}

	public void printMatrix() {
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix.length; ++j) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.print('\n');
		}
	}

	public long[][] getMatr() {
		return matrix;
	}

}
