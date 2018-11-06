package de.consol.RestServiceDemo;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FreeDiskSpaceCollector extends Collector {

	private File partition = Paths.get("/data").toFile();

	@Override
	public List<MetricFamilySamples> collect() {
		List<MetricFamilySamples> mfs = new ArrayList<>(1);
		mfs.add(new GaugeMetricFamily("free_disk_space_percent_metric", "Percent of free disk space", this.getFreeDiskSpace()));
		return mfs;
	}

	private double getFreeDiskSpace() {
		return (double) partition.getFreeSpace() / partition.getTotalSpace() * 100;
	}
}
