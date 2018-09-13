package io.bootique.job.zookeeper.it.job;

import io.bootique.job.BaseJob;
import io.bootique.job.JobMetadata;
import io.bootique.job.SerialJob;
import io.bootique.job.runnable.JobResult;
import io.bootique.job.zookeeper.it.ZkJobLockIT;

import java.util.Map;

@SerialJob
public class LockJob extends BaseJob {

    private static final int DELAY = 3_000;

    public LockJob() {
        super(JobMetadata.build(LockJob.class));
    }

    @Override
    public JobResult run(Map<String, Object> params) {
        Integer callsCount = (Integer) params.get(ZkJobLockIT.CALLS_COUNT);
        params.put(ZkJobLockIT.CALLS_COUNT, callsCount + 1);
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            return JobResult.failure(JobMetadata.build(LockJob.class));
        }
        return JobResult.success(JobMetadata.build(LockJob.class));
    }
}

