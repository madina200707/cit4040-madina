1 error Missing Semicolon
{

    public static void main(String[] args) {

        String name = "Madina";

        int a = 12;
        int b = 30;

        System.out.println("Hi, " + name)

        System.out.println("a + b = " + (a + b));

    }

}

Answer: java: ';' expected

2 error
public class Main {

    public static void main(String[] args) {

        String name = "Madina";

        int a = 12;
        int b = 30;

        System.out.printline("Hi, " + name);

        System.out.println("a + b = " + (a + b));

    }

}

Answer: java: cannot find symbol
symbol:   method printline(java.lang.String)
location: variable out of type java.io.PrintStream

3 error
public class Main {

    public static void main(String[] args) {

        String name = "Madina";

        int a = 12;
        int b = 30;

        int pages = "many";
        System.out.println("Hi, " + name);

        System.out.println("a + b = " + (a + b));

    }

}

Answer: java: incompatible types: java.lang.String cannot be converted to int


4 error

public class Main {

    public static void main(String[] args) {
        String title;
        System.out.println(title.length());

        String name = "Madina";

        int a = 12;
        int b = 30;

        System.out.println("Hi, " + name);

        System.out.println("a + b = " + (a + b));

    }

}

Answer: java: variable title might not have been initialized