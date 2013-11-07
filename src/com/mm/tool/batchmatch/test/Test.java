package com.mm.tool.batchmatch.test;

public class Test extends Thread {
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("test");
		}
	}

	public static void main(String[] args) throws InterruptedException {
		new Test().start();
		Thread.sleep(3000);
		System.out.println("return");
		//return;
		//System.exit(0);
	}
}