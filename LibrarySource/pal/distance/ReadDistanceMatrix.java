// ReadDistanceMatrix.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)


package pal.distance;

import pal.io.*;
import pal.misc.*;

import java.io.*;


/**
 * reads pairwise distance matrices in PHYLIP format
 *  (full matrix)
 *
 * @version $Id: ReadDistanceMatrix.java,v 1.3 2001/07/13 14:39:13 korbinian Exp $
 *
 * @author Korbinian Strimmer
 * @author Alexei Drummond
 */
public class ReadDistanceMatrix extends DistanceMatrix
{
	//
	// Public stuff
	//

	/** read from stream */
	public ReadDistanceMatrix(PushbackReader input)
		throws DistanceParseException
	{
		readSquare(input);
	}

	/** read from file */
	public ReadDistanceMatrix(String file)
		throws DistanceParseException, IOException
	{
		PushbackReader input = InputSource.openFile(file);
		readSquare(input);
		input.close();
	}


	//
	// Private stuff
	//

	// Read square matrix
	private void readSquare(PushbackReader in)
		throws DistanceParseException
	{
		FormattedInput fi = FormattedInput.getInstance();
		
		try
		{
			// Parse PHYLIP header line
			numSeqs = fi.readInt(in);
			fi.nextLine(in);

			// Read distance and sequence names
			distance = new double[numSeqs][numSeqs];
 			idGroup = new SimpleIdGroup(numSeqs);
			for (int i = 0; i < numSeqs; i++)
			{
				idGroup.setIdentifier(i, new Identifier(fi.readLabel(in, 10)));				
				for (int j = 0; j < numSeqs; j++)
				{
					distance[i][j] = fi.readDouble(in);
				}
				fi.nextLine(in);
			}
		}
			catch (IOException e)
			{
				throw new DistanceParseException("IO error");
			}
			catch (NumberFormatException e)
			{
				throw new DistanceParseException("Number format error");
			}
	}
}

