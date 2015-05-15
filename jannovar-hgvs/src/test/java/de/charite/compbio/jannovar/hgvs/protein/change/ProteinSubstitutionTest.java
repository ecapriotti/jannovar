package de.charite.compbio.jannovar.hgvs.protein.change;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.protein.ProteinPointLocation;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinSubstitution;

public class ProteinSubstitutionTest {

	private ProteinSubstitution sub1;
	private ProteinSubstitution sub2;
	private ProteinSubstitution sub3;

	@Before
	public void setUp() {
		sub1 = new ProteinSubstitution(true, new ProteinPointLocation("A", 123), "G");
		sub2 = new ProteinSubstitution(true, new ProteinPointLocation("A", 123), "G");
		sub3 = new ProteinSubstitution(true, new ProteinPointLocation("A", 123), "T");
	}

	@Test
	public void testEquals() {
		Assert.assertTrue(sub1.equals(sub2));
		Assert.assertTrue(sub2.equals(sub1));
		Assert.assertFalse(sub1.equals(sub3));
		Assert.assertFalse(sub3.equals(sub1));
	}

	@Test
	public void testToHGVSString() {
		Assert.assertEquals("(A124G)", sub1.toHGVSString(AminoAcidCode.ONE_LETTER));
		Assert.assertEquals("(Ala124Gly)", sub1.toHGVSString(AminoAcidCode.THREE_LETTER));
		Assert.assertEquals("(Ala124Gly)", sub1.toHGVSString());
	}

	@Test
	public void testFactoryMethod() {
		Assert.assertEquals(sub1, ProteinSubstitution.build(true, "A", 123, "G"));
	}
}
