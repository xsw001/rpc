package com.xsw.starter;
/*
小明和小红是两位魔法师，他们一起在一张 N×N 的方格地图上冒险 (N≤9)去击败魔王，地图中的方格代表他们所走的路径，方格中的正整数代表可获得的法力值，而其他的方格中数字为0代表无法获得法力值。如下图所示（见样例）:

起
 0  0  0 0 0 0
 0 12 0 0 0 8
 0  0  0 0 6 0
 0  0  5 0 0 0
 0 0 13 0 0 5
 0 0   0 0 0 0
                魔



他们二人从图上的起点出发，为了更快的打败魔王他只能向右或者向下前进，直到到达下方的魔王城。在走过的路上，他们能够获得地图里的数字（获取后地图中将变为数字 0）变为法力值。

小明先从起点出发走到魔王城，然后小红再出发到达魔王城，为了更好的对抗魔王，请你帮帮2位魔法师使其获得的法力值为最大


作答提示：
1、请在提交题目前，请务必点击保存并调试后在提交题目，否则你编写的代码将不会被保存；

2、参赛者须对编写的代码真实性负责。所有解答提交都会自动经过中兴捧月竞赛组评委严格的人工+智能作弊侦测方式进行查重，如有用户被检查出竞赛中存在违规行为（如抄袭、作弊等），我们会坚持以 零容忍的态度维护竞赛的公平、公正，取消作弊者成绩。

输入描述：

第一行为1个整数N(0<N<=9)表示 N×N 的地图，然后输入若干行，每行中有3个整数，前2个表示位置坐标x,y(1<=x,y<=N)，第3个数为该坐标提供的法力值k(0<=k<65536)。最后一行数输入单独的一行 0 0 0 表示输入结束

输出描述：

只需输出一个整数，表示小明和小红能获得的最大的法力值的和。

样例1

输入
7
2 1 12
2 5 8
2 6 6
3 3 5
4 3 13
6 6 5
7 2 1
0 0 0
输出
49
样例2

输入
5
1 1 5
1 3 6
2 5 6
4 2 13
5 1 5
0 0 0
输出
30
 */

import java.util.*;

public class Main {

    static int max = 0;
    static ArrayList<int[]> path = new ArrayList<>();

    public static void main(String[] args) {
        max = 0;
        path = new ArrayList<>();
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[][] arr = new int[n][n];
        int a, b, c;
        do {
            a = in.nextInt();
            b = in.nextInt();
            c = in.nextInt();
            if (a != 0 || b != 0 || c != 0)
                arr[a - 1][b - 1] = c;
        } while (a != 0 || b != 0 || c != 0);
        System.out.println(Arrays.deepToString(arr));
        int ans = 0;
        System.out.println(Arrays.deepToString(arr));
        help(arr, 0, 0, 0, new ArrayList<>());
        System.out.println(Arrays.deepToString(arr));
        ans += max;
        max = 0;
        for (int[] ints : path) {
            arr[ints[0]][ints[1]] = 0;
        }
        System.out.println(Arrays.deepToString(arr));
        help(arr, 0, 0, 0, new ArrayList<>());
        System.out.println(Arrays.deepToString(arr));
        ans += max;
        System.out.println(ans);
    }

    private static void help(int[][] arr, int val, int x, int y, ArrayList<int[]> list) {
        if (arr[x][y] != 0) {
            list.add(new int[]{x, y});
            val += arr[x][y];
            if (val > max) {
                max = val;
                path = new ArrayList<>(list);
            }
        }
        if (x < arr.length - 1)
            help(arr, val, x + 1, y, new ArrayList<>(list));
        if (y < arr.length - 1)
            help(arr, val, x, y + 1, new ArrayList<>(list));
    }

}
