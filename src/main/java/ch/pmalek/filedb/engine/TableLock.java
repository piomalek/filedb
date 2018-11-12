package ch.pmalek.filedb.engine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
// table used to lock whole database table in case of write operation, reads are permited anytime
public class TableLock {
    // todo - do something about starvation of the write process

    boolean isWriteLocked = false;
    int readers = 0;

    public synchronized void acquireReadLock() throws InterruptedException {
        log.info("Acquiring read lock");

        while (isWriteLocked) {
            wait();
        }

        readers++;
    }

    public synchronized void releaseReadLock() {
        log.info("Releasing read lock");

        readers--;
        notify();
    }

    public synchronized void acquireWriteLock() throws InterruptedException {
        log.info("Acquiring read lock");

        while (isWriteLocked || readers != 0) {
            wait();
        }

        isWriteLocked = true;
    }

    public synchronized void releaseWriteLock() {
        log.info("Releasing write lock");

        isWriteLocked = false;
        notify();
    }
}
