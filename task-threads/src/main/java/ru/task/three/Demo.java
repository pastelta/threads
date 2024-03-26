package ru.task.three;

public class Demo {
    public static void main(String[] args) throws InterruptedException {

        Fraction fr = new Fraction(2, 3);
        Fractionable num = Utils.cache(fr);
        num.doubleValue(); //sout - invoke double value
        num.doubleValue(); //sout is silent

        num.setNum(5);
        num.doubleValue(); //sout - invoke double value
        num.doubleValue(); //sout is silent

        num.setNum(2);
        num.doubleValue(); //sout is silent
        num.doubleValue(); //sout is silent

        Thread.sleep(1500);

        num.doubleValue(); //sout - invoke double value
        num.doubleValue(); //sout is silent
        num.doubleValue(); //sout is silent
        num.doubleValue(); //sout is silent
    }
}
