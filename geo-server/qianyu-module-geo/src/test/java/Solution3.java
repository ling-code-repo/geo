public class Solution3 {
    public long trailingZeros(long n) {
        long count = 0;
        while (n > 0) {
            n /= 5;          // 相当于计算 n/5, n/25, n/125, ...
            count += n;      // 累加当前阶的个数
        }
        return count;
    }
    
    // 测试
    public static void main(String[] args) {
        Solution3 sol = new Solution3();
        
        // 样例 1
        System.out.println(sol.trailingZeros(5));   // 输出: 1
        
        // 样例 2
        System.out.println(sol.trailingZeros(11));  // 输出: 2
        
        // 更多测试
        System.out.println(sol.trailingZeros(25));  // 输出: 6 (25! 有6个尾部0)
        System.out.println(sol.trailingZeros(100)); // 输出: 24
    }
}