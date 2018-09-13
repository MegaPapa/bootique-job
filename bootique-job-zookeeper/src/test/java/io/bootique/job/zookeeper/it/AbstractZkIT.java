package io.bootique.job.zookeeper.it;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import io.bootique.BQRuntime;
import io.bootique.curator.CuratorModule;
import io.bootique.job.runtime.JobModule;
import io.bootique.job.scheduler.Scheduler;
import io.bootique.job.zookeeper.ZkJobModule;
import io.bootique.job.zookeeper.it.job.LockJob;
import io.bootique.test.junit.BQTestFactory;
import org.junit.ClassRule;
import org.junit.Rule;
import org.testcontainers.containers.GenericContainer;

import java.io.File;
import java.util.function.Consumer;

public abstract class AbstractZkIT {

    private static final int HOST_PORT_1 = 2181;
    private static final int CONTAINER_EXPOSED_PORT_1 = 2181;
    private static final Consumer<CreateContainerCmd> MAPPING_CMD =
            e -> e.withPortBindings(
                    new PortBinding(
                            Ports.Binding.bindPort(HOST_PORT_1),
                            new ExposedPort(CONTAINER_EXPOSED_PORT_1)
                    )
            );

    @ClassRule
    public static GenericContainer zookeeper = new GenericContainer("zookeeper:latest")
            .withCreateContainerCmdModifier(MAPPING_CMD)
            .withExposedPorts(CONTAINER_EXPOSED_PORT_1);

    @Rule
    public BQTestFactory testFactory = new BQTestFactory();

    protected Scheduler getSchedulerFromRuntime(String yamlConfigPath) {
        BQRuntime bqRuntime = testFactory
                .app(yamlConfigPath)
                .override(JobModule.class).with(ZkJobModule.class)
                .module(new JobModule())
                .module(new CuratorModule())
                .module(b -> JobModule.extend(b).addJob(LockJob.class))
                .createRuntime();
        return bqRuntime.getInstance(Scheduler.class);
    }
}
