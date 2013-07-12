package jannovar.genotype;

import java.util.ArrayList;
import java.util.Iterator;


import jannovar.common.Genotype;


/**
 * This class is intended to encapsulate a genotype for a single
 * variant (i.e., line in a VCF file) for a VCF file with a single or will multiple
 * samples. The individual calls for each sample are stored in 
 * {@link #callList}, and the corresponding qualities are stored in {@link #qualityList}.
 * <P>
 * TODO: Probably it will be good to make a separate class to store the quality of the
 * calls in a more sophisticated way.
 * <P>
 * Note that this class was renamed from MultipleGenotypwe on 9 May, 2013, and the
 * class SingleGenotype was merged into it.
 * @author Peter Robinson
 * @version 0.08 (17 June, 2013)
 */
public class GenotypeCall  {

  
    /**  List of genotype calls (See {@link jannovar.common.Genotype Genotype})
     * for one variant.
     */
    private ArrayList<Genotype> callList = null;
    /**
     * List of Phred-scaled qualities for the genotype calls in {@link #callList}.
     */
    private ArrayList<Integer> qualityList = null;
    
    /**
     * The constructor takes lists of calls and qualities that have been parsed from 
     * a single VCF line by the {@link jannovar.genotype.MultipleGenotypeFactory MultipleGenotypeFactory}.
     * By assumption, there are multiple samples, which are described elsewhere by a PED file.
     * @param calls A list of the genotype calls, one for each sample
     * @param qualities A list of the genotype Phred qualities, one for each sample.
     */
    public GenotypeCall(ArrayList<Genotype> calls,ArrayList<Integer> qualities) {
	this.callList = calls;
	this.qualityList = qualities;
	//System.out.println("Warning: GenotypeCall  not fully implemented");
    }


    /**
     * This constructor is inteded to be used for VCF files with a single
     * sample, which by assumption contains data from a patient.
     */
    public GenotypeCall(Genotype gt, Integer qual) {
	this.callList = new ArrayList<Genotype>();
	this.callList.add(gt);
	this.qualityList = new ArrayList<Integer>();
	this.qualityList.add(qual);
    }


    /**
     * @return A list of genotype calls, e.g., "0/0","0/1","1/1"
     */
    public ArrayList<String> getGenotypeList() {
	ArrayList<String> lst = new ArrayList<String>();
	Iterator<Genotype> it = callList.iterator();
	while (it.hasNext()) {
	    Genotype call = it.next();
	    switch (call) {
	    case HOMOZYGOUS_REF: lst.add("0/0"); break;
	    case HOMOZYGOUS_ALT: lst.add("1/1"); break;
	    case HETEROZYGOUS: lst.add("0/1"); break;
	    case NOT_OBSERVED: lst.add("./."); break;  
	    case ERROR: lst.add("?"); break;  
	    case UNINITIALIZED: lst.add("-");
	    }
	}
	return lst;

    }
   
    /**
     * @return A string with all genotype calls separated by ":", e.g., "0/0:0/1:1/1"
     */
    public String get_genotype_as_string() {
	StringBuffer sb = new StringBuffer();
	Iterator<Genotype> it = callList.iterator();
	int c=0;
	while (it.hasNext()) {
	    Genotype call = it.next();
	    if (c++>0) sb.append(":");
	    switch (call) {
	    case HOMOZYGOUS_REF: sb.append("0/0"); break;
	    case HOMOZYGOUS_ALT: sb.append("1/1"); break;
	    case HETEROZYGOUS: sb.append("0/1"); break;
	    case NOT_OBSERVED: sb.append("./."); break;  
	    case ERROR: sb.append("?"); break;  
	    case UNINITIALIZED: sb.append("-");
	    }
	    
	}
	return sb.toString();
    }
    
    /**
     * Note that this function expects the parameter n to be zero-based.
     * If the n is invalid, return null.
     * This method is intended mainly for debugging. May need to add exceptions.
     * @return the Genotype of the Nth individual represented in the VCF file. (0-based)
     */
    public Genotype getGenotypeInIndividualN(int n){
	if (n<0 || n>=this.callList.size() )
	    throw new IllegalArgumentException();
	return this.callList.get(n);
    }

   

    /**
     * This method gets the number of individuals included in the
     * genotype call. This must be equal to the number of samples
     * in the VCF file.
     */
    public int getNumberOfIndividuals() {return this.callList.size(); }

}