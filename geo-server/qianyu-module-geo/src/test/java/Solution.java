public class Solution {
    /**
     * 判断 T 是否是 S 的子序列
     */
    public boolean canTransform(String S, String T) {
        int i = 0, j = 0;
        int n = S.length(), m = T.length();
        
        while (i < n && j < m) {
            // 如果字符匹配，移动 T 的指针
            if (S.charAt(i) == T.charAt(j)) {
                j++;
            }
            // 总是移动 S 的指针
            i++;
        }
        
        // 如果 T 的所有字符都匹配完了，说明 T 是 S 的子序列
        return j == m;
    }
    
    // 测试
    public static void main(String[] args) {
        Solution sol = new Solution();
        
        // 样例1
        String S1 = "lintcode";
        String T1 = "lint";
        System.out.println(sol.canTransform(S1, T1)); // true
        
        // 样例2
        String S2 = "lintcode";
        String T2 = "ide";
        System.out.println(sol.canTransform(S2, T2)); // true
        
        // 样例3
        String S3 = "adda";
        String T3 = "aad";
        System.out.println(sol.canTransform(S3, T3)); // false
    }
}