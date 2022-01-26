package sync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


public class Test {
    public static void main(String [] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ReadWriteLock RW = new ReadWriteLock();


        executorService.execute(new Writer(RW));
        executorService.execute(new Writer(RW));
        executorService.execute(new Writer(RW));
        executorService.execute(new Writer(RW));

        executorService.execute(new Reader(RW));
        executorService.execute(new Reader(RW));
        executorService.execute(new Reader(RW));
        executorService.execute(new Reader(RW));


    }
}


class ReadWriteLock{
    private Semaphore writer=new Semaphore(1);
    private Semaphore reader=new Semaphore(1);
    private  int readCount=0;

    public void readLock() {
        try {
            reader.acquire();
        } catch (InterruptedException e) {
            System.out.println("Thread " + Thread.currentThread().getName() + " is" +"not working");
        }
        readCount++;
        if (readCount ==1) {
            try {
                writer.acquire();
            } catch (InterruptedException e) {
                System.out.println("Thread " + Thread.currentThread().getName() + " is" +"not working");
            }
        }
        System.out.println("Thread " + Thread.currentThread().getName() + " is READING.");
        reader.release();

    }
    // READLOCK
    // We are using the reader variable to change something in the readerCount variable.
    // This is done because if some process is changing something in the readCount variable, then no other process should be allowed to use that variable.
    // So, to achieve mutual exclusion, we are using the reader variable.
    //Initially, we are calling the reader.acquire(); function and this will reduce the value of the reader by one. After that, the readCount value will be increased by one.
    //If the readCount variable is equal to "1" i.e. the reader process is the first process, in this case, no other process demanding for write operation will be allowed to enter into the critical section.
    // So, the writer.acquire(); will be called and the value of the writer variable will be decreased to "0" and no other process demanding for write operation will be allowed.
    //After changing the readerCount variable, the value of the reader variable will be increased by one, so that other processes should be allowed to change the value of the readerCount value.
    public void writeLock() {
        try {
            writer.acquire();
        } catch (InterruptedException e) {
            System.out.println("Thread " + Thread.currentThread().getName() + " is" +"not working");
        }
        System.out.println("Thread " + Thread.currentThread().getName() + " is WRITING.");
    }
    // WRITELOCK
    // The writer.acquire(); function is called so that it achieves the mutual exclusion.
    // The acquire() function will reduce the writer value to "0" and this will block other processes to enter into the critical section.
    public void readUnLock() {
        try {
            reader.acquire();
        } catch (InterruptedException e) {
            System.out.println("Thread " + Thread.currentThread().getName() + " is" +"not working");
        }
        readCount--;
        if (readCount ==0) {
            writer.release();
        }
        System.out.println("Thread " + Thread.currentThread().getName() + " is DONE READING.");
        reader.release();

    }
    //READUNLOCK
    // The read operation by various processes will be continued and after that when the read operation is done,
    // then again we have to change the count the value of the readCount and decrease the value by one.
    //If the readCount becomes "0", then we have to increase the value of the writer variable by one by calling the writer.release() function.
    // This is done because if the readerCount is "0" then other writer processes should be allowed to enter into the critical section to write the data in the file.

    public void writeUnLock() {
        System.out.println("Thread " + Thread.currentThread().getName() + " is DONE WRITING.");
        writer.release();

    }
    //WRITEUNLOCK
    // The write operation will be carried and finally, the writer.release() function will be called
    // and the value of the writer will be again set to "1" and now other processes will be allowed to enter into the critical section.

}




class Writer implements Runnable
{
    private ReadWriteLock RW_lock;


    public Writer(ReadWriteLock rw) {
        RW_lock = rw;
    }

    public void run() {
        while (true){
            SleepUtilities.nap();
            RW_lock.writeLock();
            SleepUtilities.nap();
            RW_lock.writeUnLock();

        }
    }
}
// In writer class we call writer functions writeLock  and writeUnclock .
//writeLock is a function prevents other writers and readers from accessing the system while the writer is writing.
//writeUnlock is a  function allows other authors and readers to access the system when the author has completed the writing process.



class Reader implements Runnable
{
    private ReadWriteLock RW_lock;


    public Reader(ReadWriteLock rw) {
        RW_lock = rw;
    }
    public void run() {
        while (true){
            SleepUtilities.nap();
            RW_lock.readLock();

            SleepUtilities.nap();
            RW_lock.readUnLock();


        }
    }
}
//// In reader class we call writer functions readLock  and readUnclock .
// readLock is a function allows one reader to read while the other reader is reading, but writers are not allowed until the read is complete.
// readUnLock is a function allows other readers and writers to take action when one reader completes the read operation.
class SleepUtilities
{
    public static void nap() {
        nap(NAP_TIME);
    }

    public static void nap(int duration) {
        int sleeptime = (int) (NAP_TIME * Math.random() );
        try { Thread.sleep(sleeptime*1000); }
        catch (InterruptedException e) {
            System.out.println("Thread " + Thread.currentThread().getName() + " is" +"not working");
        }
    }

    private static final int NAP_TIME = 5;
    //Nap time is a variable First we call the normal method.
    // Then our normal method calls the constructor.
    // Construct picks a random number and sleeps the current thread accordingly.
}


