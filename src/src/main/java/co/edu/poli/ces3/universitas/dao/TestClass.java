package co.edu.poli.ces3.universitas.dao;

public class TestClass {
    public static int N = 5;
    private int number1;
    public float number2;
    protected boolean isNumber;
    String name;
    String lastName;
    private int[] numbers;

    TestClass(){
        number1 = 12;
        number2 = 3;
        name = "oscar";
        isNumber = true;
        numbers = new int[N];
        numbers[0] = 100;
        numbers[N - 1] = 200;
    }
    TestClass(String name, int N){
        this.name = name;
        this.numbers = new int[N];
    }

    TestClass(String name){
        isNumber = false;
        System.out.println(this.N);
    }

    TestClass(boolean isNumber){
        this.isNumber = isNumber;
    }

    public void initArray(){
        this.numbers = new int[this.N+1];
        for (int i = 1;i<=N; i = i+1){
            this.numbers[i] = (int) (Math.random()*20+1);
        }
    }

    public static void main(String[] args) {
        TestClass cnn = new TestClass("Pedro", 900);
        cnn.initArray();
        System.out.println("Hola CES3" + TestClass.N);
        System.out.println(cnn);
    }
}
