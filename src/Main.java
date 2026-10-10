public class Main {


    public static int calculateReadingTime(int pages) {
        int baseTime = pages * 2;

        if (pages > 500) {

            return baseTime + (baseTime / 10);
        }

        return baseTime;
    }

    public static void main(String[] args) {

        String title = "Java Practice";
        System.out.println(title.length());

        String name = "Madina";
        int a = 12;
        int b = 30;

        System.out.println("Hi, " + name);
        System.out.println("a + b = " + (a + b));


        System.out.println("100 pages -> " + calculateReadingTime(100) + " min");
        System.out.println("500 pages -> " + calculateReadingTime(500) + " min");
        System.out.println("800 pages -> " + calculateReadingTime(800) + " min");
    }
}