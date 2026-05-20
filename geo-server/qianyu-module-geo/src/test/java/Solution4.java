import java.util.*;

public class Solution4 {
    public boolean[] isAttacked(int[][] queens, int[][] knights) {
        int M = knights.length;
        boolean[] res = new boolean[M];
        
        // 四个哈希集合
        Set<Integer> rows = new HashSet<>();
        Set<Integer> cols = new HashSet<>();
        Set<Long> mainDiag = new HashSet<>(); // y - x
        Set<Long> antiDiag = new HashSet<>(); // y + x
        
        // 记录皇后的攻击线
        for (int[] q : queens) {
            int x = q[0];
            int y = q[1];
            rows.add(y);               // 行 = y 坐标
            cols.add(x);               // 列 = x 坐标
            mainDiag.add((long) y - x);
            antiDiag.add((long) y + x);
        }
        
        // 检查每个骑士
        for (int i = 0; i < M; i++) {
            int x = knights[i][0];
            int y = knights[i][1];
            
            if (rows.contains(y) || cols.contains(x) || 
                mainDiag.contains((long) y - x) || 
                antiDiag.contains((long) y + x)) {
                res[i] = true;
            } else {
                res[i] = false;
            }
        }
        
        return res;
    }
    
    // 测试
    public static void main(String[] args) {
        Solution4 sol = new Solution4();
        
        int[][] queens = {{1, 1}, {2, 2}};
        int[][] knights = {{3, 3}, {1, 3}, {4, 5}};
        
        boolean[] result = sol.isAttacked(queens, knights);
        // 输出 [true, true, false]
        System.out.println(Arrays.toString(result));
    }
}