package ie.gmit.sw;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

public class Utils {

	public static String getResourceDir() {
		String resDir = System.getProperty("user.dir") + File.separatorChar + "resources" + File.separatorChar;
		return resDir;
	}

	public static int[] chunkStringChars(char[] data, int chunkSize) {

		// First convert the string to a char array
		// get the length of this char array
		int len = data.length;
		// create a return array of appropriate size
		int[] result = new int[(len + chunkSize - 1) / chunkSize];
		// create a temp array to hold chars from data array
		char[] a;
		int retArrayIndex = 0;
		for (int i = 0; i < len; i += chunkSize) {
			// get only the exact number of chars we need
			a = Arrays.copyOfRange(data, i, i + chunkSize);
			// then hash these chars and place them in return array at correct index
			result[retArrayIndex] = Arrays.hashCode(a);
			retArrayIndex++;
		}
		return result;
	}

	public static int[] chunkStringChars(String text, int chunkSize) {

		// First convert the string to a char array
		char[] data = text.toCharArray();
		// get the length of this char array
		int len = data.length;
		// create a return array of appropriate size
		int[] result = new int[(len + chunkSize - 1) / chunkSize];
		// create a temp array to hold chars from data array
		char[] a;
		int retArrayIndex = 0;
		for (int i = 0; i < len; i += chunkSize) {
			// get only the chars we need
			a = Arrays.copyOfRange(data, i, i + chunkSize);
			// then hash these chars and place them in return array at correct index
			result[retArrayIndex] = Arrays.hashCode(a);
			retArrayIndex++;
		}
		return result;
	}

	public static String fractionToPercent(double input) {
		DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.CEILING);
		return df.format(input * 100) + "%";
	}

	public static String[] chunkString(String text, int chunkSize) {

		char[] data = text.toCharArray();
		int len = data.length;

		String[] result = new String[(len + chunkSize - 1) / chunkSize];
		int linha = 0;
		for (int i = 0; i < len; i += chunkSize) {
			result[linha] = new String(data, i, Math.min(chunkSize, len - i));
			linha++;
		}
		return result;
	}

}