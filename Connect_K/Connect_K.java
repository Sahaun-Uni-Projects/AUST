/**
 * @author Sohom Sahaun
 */

import java.util.*;

class Macro {
    public static final boolean VERBOSE = true;
    public static final boolean HIDE_CELL_NUMBER = false;
    public static final char DEF_CHAR = '.';
}

class Global {
    public static int match;
}

class Util {
    public static int to_int(String str) {
        if (str.isEmpty()) return -1;
        
        char c = str.charAt(0);
        
        if (is_between(c, '1', '9')) {
            int n = Integer.parseInt(str);
            if (is_between(n, 1, 15)) return n;
        }
        
        if (is_between(c, 'a', 'f')) return ((int)(c - 'a') + 10);
        if (is_between(c, 'A', 'F')) return ((int)(c - 'A') + 10);
        
        return -1;
    }
    
    public static int to_int(boolean n) {
        return ((n == true) ? 1 : 0);
    }
    
    public static char to_hex(int n) {
        return ((char)(n + ((n < 10) ? '0' : ('A'-10))));
    }
    
    public static boolean is_between(int n, int a, int b) {
        return ((a <= n) & (n <= b));
    }
    
    public static boolean is_between(char n, char a, char b) {
        return ((a <= n) & (n <= b));
    }
    
    public static void new_line(int n) {
        for (int i = 0; i < n; ++i) System.out.print("\n");
    }
    
    public static void draw_line(int n) {
        for (int i = 0; i < n; ++i) System.out.print("-");
        System.out.print("\n");
    }
}

class Grid {
    private static char[][] grid;
    private static int rows, cols;
    
    public static void init(int r, int c) {
        if (Macro.VERBOSE) System.out.println("Initializing Grid (" + r + "x" + c + ")...");
        
        rows = r;
        cols = c;
        grid = new char[r+1][c+1];
        
        clear();
        
        if (Macro.VERBOSE) {
            System.out.println("Grid Initialized!");
            Util.new_line(2);
        }
    }
    
    private static void clear() {
        if (Macro.VERBOSE) System.out.print("Clearing Grid...");
          
        for (int r = 0; r <= rows; ++r) {
            for (int c = 0; c <= cols; ++c) {
                if (r == 0) grid[r][c] = Util.to_hex(c);
                    else if (c == 0) grid[r][c] = Util.to_hex(r);
                    else grid[r][c] = Macro.DEF_CHAR;
            }
        }
        
        if (Macro.VERBOSE) System.out.println(" Complete!");
    }
    
    public static void show() {
        Util.new_line(1);
        for (int r = Util.to_int(Macro.HIDE_CELL_NUMBER); r <= rows; ++r) {
            for (int c = Util.to_int(Macro.HIDE_CELL_NUMBER); c <= cols; ++c) {
                System.out.print(grid[r][c] + " ");
                if (c == 0) System.out.print(" ");
            }
            Util.new_line(1);
            if (r == 0) Util.new_line(1);
        }
    }
    
    public static int get_total_rows() {
        return rows;
    }
    
    public static int get_total_columns() {
        return cols;
    }
    
    public static void set_char_at(char ch, int row, int col) {
        grid[row][col] = ch;
    }
    
    public static char get_char_at(int row, int col) {
        return grid[row][col];
    }
}

abstract class Player {
    private char ch;
    private boolean winState = false;
    
    Player(char ch) {
        this.ch = ch;
    }
    
    public void set_char(char ch) {
        this.ch = ch;
    }
    
    public char get_char() {
        return this.ch;
    }
    
    public boolean get_win_state() {
        return this.winState;
    }
    
    public boolean place(int col) {
        int row = 0,
            totalRows = Grid.get_total_rows(),
            totalCols = Grid.get_total_columns();
        
        if (!Util.is_between(col, 1, totalCols)) {
            Util.new_line(1);
            System.out.println("Invalid Token.");
            String str = "Valid inputs are 1-" + totalCols;
            if (totalCols == 10) str += ", 'A', 'a'";
                else if (totalCols > 10) str += ", 'A'-'" + (char)('A'+(totalCols-10))+ "', 'a'-'" + (char)('a'+(totalCols-10)) + "'";
            System.out.println(str + ".");
            return false;
        }
        
        while (++row <= totalRows) {
            if (Grid.get_char_at(row, col) != Macro.DEF_CHAR) break;
        }
        
        if (row == 1) {
            System.out.println("Invalid Action. Column " + col + " is full.");
            Util.new_line(1);
            return false;
        }
        
        --row;
        Grid.set_char_at(this.ch, row, col);
        this.winState = check_win(row, col);
        
        Grid.show();
        return true;
    }
    
    private boolean check_win(int row, int col) {
        int r, c, cnt,
            totalRows = Grid.get_total_rows(),
            totalCols = Grid.get_total_columns();
        
        
        for (r = row, c = col, cnt = 0; r <= totalRows; ++r) {
            if (Grid.get_char_at(r, c) == this.ch) {
                if (++cnt >= Global.match) return true;
            } else cnt = 0;
        }
        
        
        for (r = row, c = 1, cnt = 0; c <= totalCols; ++c) {
            if (Grid.get_char_at(r, c) == this.ch) {
                if (++cnt >= Global.match) return true;
            } else cnt = 0;
        }
        
        
        if (row > col) {
            r = row - col + 1;
            c = 1;
        } else {
            r = 1;
            c = col - row + 1;
        }
        for (cnt = 0; (r <= totalRows) && (c <= totalCols); ++r, ++c) {
            if (Grid.get_char_at(r, c) == this.ch) {
                if (++cnt >= Global.match) return true;
            } else cnt = 0;
        }
        
        
        if (totalRows-row+1 > col) {
            r = row + col - 1;
            c = 1;
        } else {
            r = totalRows;
            c = col - totalRows + row;
        }
        for (cnt = 0; (r >= 1) && (c <= totalCols); --r, ++c) {
            if (Grid.get_char_at(r, c) == this.ch) {
                if (++cnt >= Global.match) return true;
            } else cnt = 0;
        }
        
        return false;
    }
    
    abstract void debug();
    abstract String get_win_message();
}

class Player1 extends Player {
    Player1(char ch) {
        super(ch);
    }

    @Override
    void debug() {
        Util.new_line(2);
        Util.draw_line(21);
        System.out.println("Showing information for Player 1:");
        System.out.println("Char: " + this.get_char());
        Util.draw_line(21);
        Util.new_line(2);
    }

    @Override
    String get_win_message() {
        return ("Player1 (" + this.get_char() + ") has won!");
    }
}

class Player2 extends Player {
    Player2(char ch) {
        super(ch);
    }

    @Override
    void debug() {
        Util.new_line(2);
        Util.draw_line(21);
        System.out.println("Showing information for Player 2:");
        System.out.println("Char: " + this.get_char());
        Util.draw_line(21);
        Util.new_line(2);
    }

    @Override
    String get_win_message() {
        return ("Player2 (" + this.get_char() + ") has won!");
    }
}

public class Connect_K {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        boolean won = false;
        String str;
        int moveCol, rows, cols, min, turns, maxTurns;
        char char1, char2;
        
        System.out.print("Welcome to Connect-K!");
        
        Util.new_line(2);
        
        System.out.println("Enter dimensions of the Grid [3,15]:");
        
        System.out.print("Width(Columns): ");
        cols = sc.nextInt();
        while (!Util.is_between(cols, 3, 15)) {
            System.out.println("Invalid action. Width should be between 3-15.");
            System.out.print("Width(Columns): ");
            cols = sc.nextInt();
        }
        
        System.out.print("Height(Rows): ");
        rows = sc.nextInt();
        while (!Util.is_between(rows, 3, 15)) {
            System.out.println("Invalid action. Height should be between 3-15.");
            System.out.print("Height(Rows): ");
            rows = sc.nextInt();
        }
        
        Util.new_line(1);
        
        min = cols;
        if (min > rows) min = rows;
        
        System.out.print("Number of matches [3," + min + "]: ");
        Global.match = sc.nextInt();
        while (!Util.is_between(Global.match, 3, min)) {
            System.out.println("Invalid action. Matches should be between 3-" + min + ".");
            System.out.print("Number of matches [3," + min + "]: ");
            Global.match = sc.nextInt();
        }
        
        Util.new_line(1);
        
        System.out.print("Select Player 1's character: ");
        char1 = sc.next().charAt(0);
        while (char1 == Macro.DEF_CHAR) {
            System.out.println("Invalid token. Characters should be unique and not '" + Macro.DEF_CHAR + "'.");
            System.out.print("Select Player 1's character: ");
            char1 = sc.next().charAt(0);
        }
        
        System.out.print("Select Player 2's character: ");
        char2 = sc.next().charAt(0);
        while ((char2 == char1) || (char2 == Macro.DEF_CHAR)) {
            System.out.println("Invalid token. Characters should be unique and not '" + Macro.DEF_CHAR + "'.");
            System.out.print("Select Player 2's character: ");
            char2 = sc.next().charAt(0);
        }
        
        Player1 p1 = new Player1(char1);
        Player2 p2 = new Player2(char2);
        
        Player currPlayer = p1;
        maxTurns = rows*cols;
        
        Util.new_line(1);
        Grid.init(rows, cols);
        Grid.show();
        
        turns = 0;
        while (turns < maxTurns) {
            Util.new_line(1);
            if (Macro.VERBOSE) System.out.println("Player " + currPlayer.get_char() + "'s turn.");
            System.out.print("Place " + currPlayer.get_char() + " in column: ");
            
            str = sc.nextLine();
            while (str.isEmpty()) str = sc.nextLine();
            moveCol = Util.to_int(str);
            
            if (currPlayer.place(moveCol)) {
                ++turns;
                if (currPlayer.get_win_state()) {
                    won = true;
                    break;
                }
                currPlayer = (currPlayer == p1) ? p2 : p1;
            }
        }
        
        
        Util.new_line(2);
        Util.draw_line(21);
        System.out.println("The game has ended.");
        
        if (!won) System.out.println("It is a draw!");
            else System.out.println(currPlayer.get_win_message());
        
        Util.draw_line(21);
        Util.new_line(2);
    }
}