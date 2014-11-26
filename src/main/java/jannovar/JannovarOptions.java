package jannovar;

import jannovar.common.Constants.Release;

import com.google.common.net.HostAndPort;


/**
 * Configuration for the Jannovar program.
 *
 * This class contains the configuration for all Jannovar commands, even though most are not used by some commands. For
 * example, the proxy setting is only used when downloading data.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter Robinson <peter.robinson@charite.de>
 */
public class JannovarOptions {
	/** the selected command */
	public Command command = null;

	// Configuration for the download command

	/** data source to use for downloading */
	public DataSource dataSource = DataSource.UCSC;

	/** genome release to downloads and serialize */
	public Release genomeRelease = Release.HG19;

	/** directory to use for the downloads and the serialized file */
	public String downloadPath = "data";

	/** proxy host and port */
	public HostAndPort proxy = null;

	// Configuration for the annotate command

	/** path to a VCF file to be annotated */
	public String vcfFilePath = null;

	/** path to the file with the serialized data */
	public String dataFile = null;

	/** whether to write the result in the Jannovar format */
	public boolean jannovarFormat = false;

	/** whether to report the annotations for all affected transcripts */
	public boolean showAll = false;

	/** path to output folder for the annotated VCF files (default is current folder) */
	public String outVCFFolder = null;

	// TODO(holtgrem): enable and use this!
	/** path to output VCF file path (overrides generation of file name from input file name) */
	public String outVCFFile = null;

	// Configuration for the annotate-position command

	/** chromosomal position and a change, e.g. "chr1:12345C>A" */
	public String chromosomalChange = null;

	/**
	 * The command that is to be executed.
	 */
	public enum Command {
		DOWNLOAD, ANNOTATE_VCF, ANNOTATE_POSITION
	}

	/**
	 * Enumeration of the supported data sources.
	 */
	public enum DataSource {
		ENSEMBL, REFSEQ, REFSEQ_CURATED, UCSC
	}

	void print() {
		if (command == Command.DOWNLOAD) {
			System.err.println("dataSource: " + dataSource);
			System.err.println("downloadPath" + downloadPath);
			System.err.println("genome: " + genomeRelease);
			System.err.println("proxy: " + proxy);
		} else if (command == Command.ANNOTATE_VCF) {
			System.err.println("dataFile:" + dataFile);
			System.err.println("vcf:" + vcfFilePath);
			System.err.println("showAll:" + showAll);
			System.err.println("jannovarFormat:" + jannovarFormat);
		}
	}
}
