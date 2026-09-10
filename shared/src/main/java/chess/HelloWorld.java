package chess;

import java.util.Arrays;

public class HelloWorld {
    public static void main(String[] args){
        var gs = new GetSetExample();
        gs.scores[2] = 50;
        System.out.println(Arrays.toString(gs.scores));
    }
    public int[] scores = new int[10];
}
