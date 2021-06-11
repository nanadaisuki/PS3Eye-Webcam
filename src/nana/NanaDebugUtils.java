package nana;

public class NanaDebugUtils {

    static int[][] group = new int[][]{new int[60], new int[60]};
    static int index = 0;
    static int searchSize = 0;

    public static int getAge(int callId, int input) {
        int[] temp = group[callId];
        int output = 0;
        if (index == temp.length) {
            index = 0;
            searchSize = temp.length;
        }
        if (searchSize != temp.length) {
            searchSize = index + 1;
        }
        temp[index] = input;
        for (int i = 0; i < searchSize; i++) {
            output = output + temp[i];
        }
        index = index + 1;
        output = output / searchSize;
        return output;
    }
}
