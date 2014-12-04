/**
 *
 */
package jannovar.gff;

import jannovar.common.ChromosomeMap;
import jannovar.exception.FeatureFormatException;
import jannovar.exception.InvalidAttributException;
import jannovar.gff.FeatureProcessor.Gene;
import jannovar.gff.FeatureProcessor.Transcript;
import jannovar.reference.TranscriptModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the builder for the {@link TranscriptModel}s from GFF-files.
 *
 * @author Marten Jaeger <marten.jaeger@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class TranscriptModelBuilder implements ChromosomeMap {

	/** {@link Logger} to use for logging */
	private static final Logger LOGGER = Logger.getLogger(TranscriptModelBuilder.class.getSimpleName());

	/** {@link GFFVersion} to assume for building transcripts from Feature objects */
	private final GFFVersion gffVersion;

	/** the features to build the transcript models from */
	private final ArrayList<Feature> featureList;

	public TranscriptModelBuilder(GFFVersion gffVersion, ArrayList<Feature> featureList) {
		this.gffVersion = gffVersion;
		this.featureList = featureList;
	}

	/**
	 * Generates all possible {@link TranscriptModel}s from the given {@link Feature}s (also uncurated ones).
	 *
	 * If mapRna2Geneid is not null and contains appropriate values a mapping to the corresponding Entrez ids is stored.
	 *
	 * @return {@link ArrayList} with generated {@link TranscriptModel}s
	 * @throws InvalidAttributException
	 *             on problems with attributes
	 * @throws FeatureFormatException
	 *             on problems with the feature formats
	 */
	public ArrayList<TranscriptModel> make() throws InvalidAttributException, FeatureFormatException {
		return make(false);
	}

	/**
	 * Generates all possible {@link TranscriptModel}s from the given {@link Feature}s.
	 *
	 * If mapRna2Geneid is not null and contains appropriate values a mapping to the corresponding Entrez ids is stored.
	 *
	 * @param useOnlyCurated
	 *            should only curated transcript be processed (RefSeq only)
	 * @return {@link ArrayList} with generated {@link TranscriptModel}s
	 * @throws InvalidAttributException
	 *             on problems with attributes
	 * @throws FeatureFormatException
	 *             on problems with the feature formats
	 */
	public ArrayList<TranscriptModel> make(boolean useOnlyCurated) throws InvalidAttributException,
			FeatureFormatException {
		LOGGER.info("Processing features...");
		HashMap<String, Gene> genes = new FeatureProcessor(gffVersion).run(featureList);

		LOGGER.info("Building transcript models...");
		return buildTranscripts(genes, useOnlyCurated);
	}

	/**
	 * Process the <code>genes</code> and convert into an {@link ArrayList} of {@link TranscriptModel}.
	 *
	 * @param genes
	 *            the name/Gene map to build the {@link TranscriptModel} objects for.
	 * @param useOnlyCurated
	 *            whether or not to only return curated transcripts
	 * @return list of {@link TranscriptModel} objects
	 * @throws InvalidAttributException
	 *             on problems with invalid attributes
	 */
	private ArrayList<TranscriptModel> buildTranscripts(HashMap<String, Gene> genes, boolean useOnlyCurated)
			throws InvalidAttributException {
		ArrayList<TranscriptModel> models = new ArrayList<TranscriptModel>();
		TranscriptModel model;
		int curid;
		for (FeatureProcessor.Gene gene : genes.values()) {
			if (gene.id == null)
				continue;
			for (Transcript rna : gene.rnas.values()) {
				if (useOnlyCurated && !isCuratedName(rna.name))
					continue;
				model = TranscriptModel.createTranscriptModel();
				model.setAccessionNumber(rna.name);
				model.setChromosome(rna.chromosom);
				model.setGeneSymbol(gene.name);
				model.setStrand(rna.strand ? '+' : '-');
				model.setTranscriptionStart(rna.getTxStart());
				model.setTranscriptionEnd(rna.getTxEnd());

				// Check whether the corrected CDS start position returned from getCdsStart() is within an exon and do
				// the same for the CDS end position. The correction in these methods can lead to inconsistent positions
				// in the case of 3' and 5' UTR truncation.
				boolean cdsStartInExon = false;
				int cdsStart = rna.getCdsStart();
				for (int i = 0; i < rna.getExonStarts().length; ++i)
					cdsStartInExon = cdsStartInExon
							|| (cdsStart >= rna.getExonStarts()[i] && cdsStart <= rna.getExonEnds()[i]);
				boolean cdsEndInExon = false;
				int cdsEnd = rna.getCdsEnd();
				for (int i = 0; i < rna.getExonStarts().length; ++i)
					cdsEndInExon = cdsEndInExon || (cdsEnd >= rna.getExonStarts()[i] && cdsEnd <= rna.getExonEnds()[i]);
				if (!cdsStartInExon || !cdsEndInExon) {
					LOGGER.log(Level.WARNING, "Transcript {0} appears to be 3'/5' truncated. Ignoring.", rna.id);
					continue;
				}

				model.setCdsStart(rna.getCdsStart());
				model.setCdsEnd(rna.getCdsEnd());
				model.setExonCount((byte) rna.exons.size());
				model.setExonStartsAndEnds(rna.getExonStarts(), rna.getExonEnds());
				if (gffVersion.version == 3)
					model.setGeneID(Integer.parseInt(gene.id.substring(4)));
				else if ((curid = RNA2GeneIDMapper.getGeneID(gene.id)) > 0)
					model.setGeneID(curid);
				else
					throw new InvalidAttributException("Found no valid geneID mapping for accession: " + gene.id);
				models.add(model);
			}
		}

		return models;
	}

	/**
	 * Checks whether <code>rnaName</code> is the name of a curated transcript.
	 *
	 * We consider a name being non-curated if it is <code>null</code> or begins with <code>"XM_"</code>, or
	 * <code>"XR_"</code>. In all other cases, we consider a RNA name as indicating a curated entry. This only works for
	 * RefSeq names.
	 *
	 * @param rnaName
	 *            RNA name to check for matching the "is curated" pattern
	 * @return <code>true</code> if the RNA name is curated.
	 */
	private static boolean isCuratedName(String rnaName) {
		return !((rnaName == null) || rnaName.startsWith("XM_") || rnaName.startsWith("XR_"));
	}

}
