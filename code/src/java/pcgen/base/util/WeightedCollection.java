/*
 * WeightedCollection.java
 * Copyright 2007 (c) Tom Parker <thpr@users.sourceforge.net>
 *  Derived from WeightedList.java
 *  Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.util;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * An implementation of the <tt>Collection</tt> interface that allows objects
 * added to the Collection to have a &quot;weight&quot; associated with them.
 * This weight acts as though <i>weight</i> copies of the item were added to
 * the Collection. The <code>size()</code> method returns the total weight of
 * all items in the Collection. The <code>get()</code> method returns the
 * &quot;weighth&quot; element in the Collection.
 * <p>
 * As an example, if three items are added to the Collection
 * <ul>
 * <li>Item 1, weight 3</li>
 * <li>Item 2, weight 2</li>
 * <li>Item 3, weight 1</li>
 * </ul>
 * The Collection will have a total weight of 3+2+1=6. The call
 * <code>get(4)</code> will return Item 2.
 * <p>
 * 
 * @author boomer70 and Tom Parker <thpr@users.sourceforge.net>
 * @param <E>
 *            The Class stored in the WeightedCollection
 * @see java.util.Collection
 */
public class WeightedCollection<E> extends AbstractCollection<E>
{

	/**
	 * The actual list where the data is stored.
	 */
	private Collection<WeightedItem<E>> theData;

	/**
	 * Default constructor. Creates an empty collection.
	 */
	public WeightedCollection()
	{
		theData = new ArrayList<WeightedItem<E>>();
	}

	/**
	 * Constructs an empty collection with the specified initial capacity.
	 * 
	 * @param initialSize
	 *            the initial capacity of the collection.
	 * 
	 * @exception IllegalArgumentException
	 *                if the specified initial capacity is negative
	 */
	public WeightedCollection(final int initialSize)
	{
		theData = new ArrayList<WeightedItem<E>>(initialSize);
	}

	/**
	 * Creates a <tt>WeightedCollection</tt> from the <tt>Collection</tt>
	 * provided. All the elements added will have the default weight equal to
	 * the number of times they appear in the given collection.
	 * 
	 * @param c
	 *            The <tt>Collection</tt> to copy.
	 */
	public WeightedCollection(final Collection<? extends E> c)
	{
		this();
		addAll(c, 1);
	}

	/**
	 * Constructs an empty collection with the specified initial capacity.
	 * 
	 * @param comp
	 *            The Comparator this Set will use to determine equality
	 */
	public WeightedCollection(Comparator<? super E> comp)
	{
		if (comp == null)
		{
			theData = new ArrayList<WeightedItem<E>>();
		}
		else
		{
			theData =
					new TreeSet<WeightedItem<E>>(new WeightedItemComparator<E>(
						comp));
		}
	}

	/**
	 * Returns the total weight of the WeightedCollection. This is the sum of
	 * the weights of all the items in the WeightedCollection.
	 * 
	 * @return The total weight.
	 */
	@Override
	public int size()
	{
		int total = 0;
		for (WeightedItem<E> wi : theData)
		{
			total += wi.getWeight();
		}
		return total;
	}

	/**
	 * Adds all the elements from the specified <tt>Collection</tt> to this
	 * WeightedCollection with the default weight of 1.
	 * 
	 * @param c
	 *            The <tt>Collection</tt> to add the elements from.
	 * 
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(final Collection<? extends E> c)
	{
		return addAll(c, 1);
	}

	/**
	 * Adds an element to the WeightedCollection with the specified weight. If
	 * the element is already present in the WeightedCollection the weight is
	 * added to the existing element instead. Note that this is means
	 * WeightedCollection does not guarantee order of the collection.
	 * 
	 * @param weight
	 *            Weight to add this element with.
	 * @param element
	 *            Element to add.
	 * 
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public boolean add(final E element, final int weight)
	{
		if (weight <= 0)
		{
			throw new IllegalArgumentException("Cannot items with weight <= 0");
		}
		// Lets see if we can find this element
		for (final WeightedItem<E> wi : theData)
		{
			E wie = wi.getElement();
			if (wie == null && element == null || wie != null
				&& wie.equals(element))
			{
				wi.addWeight(weight);
				return true;
			}
		}
		return theData.add(new WeightedItem<E>(element, weight));
	}

	/**
	 * Adds the specified element with the default weight.
	 * 
	 * @param element
	 *            The element to add
	 * @return true if the element was added.
	 * 
	 * @see WeightedCollection#add(int, Object)
	 * @see java.util.List#add(java.lang.Object)
	 */
	@Override
	public boolean add(final E element)
	{
		return add(element, 1);
	}

	/**
	 * Returns a random selection from the WeightedCollection based on weight.
	 * 
	 * @return The random element selected.
	 */
	public E getRandomValue()
	{
		int index = RandomUtil.getRandomInt(size());
		int total = 0;
		E element = null;
		for (WeightedItem<E> wi : theData)
		{
			total += wi.getWeight();
			if (total > index)
			{
				element = wi.getElement();
				// NOTE The return statement can't be 100% covered with a Sun compiler for code coverage stats.
				// See http://sourceforge.net/tracker/index.php?func=detail&aid=1961021&group_id=25576&atid=1036937
				// for details
				return element;
			}
		}
		throw new IndexOutOfBoundsException(index + " >= " + total);
	}

	/**
	 * Returns an <tt>Iterator</tt> that iterates over the elements in the
	 * WeightedCollection. This Iterator <i>accounts for the weight of the
	 * elements in the WeightedCollection</i>.
	 * 
	 * @return An <tt>Iterator</tt> for the WeightedCollection.
	 * 
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<E> iterator()
	{
		return new WeightedIterator();
	}

	/**
	 * Returns an <tt>Iterator</tt> that iterates over the elements in the
	 * WeightedCollection. This Iterator <i>does NOT account for the weight of
	 * the elements in the WeightedCollection</i>. Therefore in a list with
	 * three elements of differing weights, this iterator simply returns each
	 * element in turn.
	 * 
	 * @return An <tt>Iterator</tt> for the WeightedCollection.
	 */
	public Iterator<E> unweightedIterator()
	{
		return new UnweightedIterator();
	}

	/**
	 * Checks if the object specified exists in this WeightedCollection.
	 * 
	 * @param o
	 *            The object to test for
	 * @return <tt>true</tt> if the object is in the WeightedCollection.
	 * 
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(final Object o)
	{
		for (final WeightedItem<E> wi : theData)
		{
			E wie = wi.getElement();
			if (wie == null && o == null || wie != null && wie.equals(o))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the weight for the given object in this WeightedCollection. If
	 * the given object is not in this collection, zero is returned.
	 * 
	 * @param o
	 *            The object for which the weight in this WeightedCollection
	 *            will be returned.
	 * @return the weight of the given object in this WeightedCollection, or
	 *         zero if the object is not in this WeightedCollection
	 */
	public int getWeight(Object o)
	{
		for (final WeightedItem<E> wi : theData)
		{
			E wie = wi.getElement();
			if (wie == null && o == null || wie != null && wie.equals(o))
			{
				return wi.theWeight;
			}
		}
		return 0;
	}

	/**
	 * Removes the object from the WeightedCollection if it is present. This
	 * removes the object from this WeightedCollection regardless of the weight
	 * of the object in this WeightedCollection. Therefore, if an object was
	 * weight 2 in this WeightedCollection and is removed, the size of this
	 * WeightedCollection will decrease by two, and NO copies of the given
	 * object will remain in this WeightedCollection.
	 * 
	 * @param o
	 *            The element to remove
	 * @return <tt>true</tt> if the element was removed.
	 * 
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(final Object o)
	{
		for (Iterator<WeightedItem<E>> i = theData.iterator(); i.hasNext();)
		{
			final WeightedItem<E> wi = i.next();
			E wie = wi.getElement();
			if (wie == null && o == null || wie != null && wie.equals(o))
			{
				i.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests if this WeightedCollection has any elements.
	 * 
	 * @return <tt>true</tt> if the WeightedCollection contains no elements.
	 * 
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return theData.isEmpty();
	}

	/**
	 * Removes all the elements from the WeightedCollection.
	 * 
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear()
	{
		theData.clear();
	}

	/**
	 * Compares the specified object with this WeightedCollection for equality.
	 * Returns <tt>true</tt> if and only if the specified object is also a
	 * WeightedCollection, both WeightedCollections have the same size, and all
	 * corresponding pairs of elements in the two WeightedCollections are
	 * <i>equal</i>. (Two elements <tt>e1</tt> and <tt>e2</tt> are <i>equal</i>
	 * if <tt>(e1==null ? e2==null :
	 * e1.equals(e2))</tt>.) In other words,
	 * two WeightedCollections are defined to be equal if they contain the same
	 * elements in the same order.
	 * <p>
	 * 
	 * @param o
	 *            The object to be compared for equality with this
	 *            WeightedCollection.
	 * @return <tt>true</tt> if the specified object is equal to this
	 *         WeightedCollection.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o)
	{
		/*
		 * CONSIDER Currently, this is ORDER SENSITIVE, which is probably bad
		 * for a collection? This needs to be seriously thought through to
		 * determine how exactly this should work... especially given that there
		 * is no solution for sorting a WeightedCollection and thus it is not
		 * possible to actually sort before doing the comparison. - thpr 2/5/07
		 */
		return o instanceof WeightedCollection
			&& theData.equals(((WeightedCollection) o).theData);
	}

	/**
	 * Returns the hash code value for this WeightedCollection.
	 * <p>
	 * 
	 * @return the hash code value for this WeightedCollection.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return theData.hashCode();
	}

	/**
	 * Returns a string representation of the WeightedCollection.
	 * 
	 * @return A string representation of the values in the WeightedCollection.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@SuppressWarnings("nls")
	@Override
	public String toString()
	{
		return "WeightedCollection: " + theData.toString();
	}

	/**
	 * Adds each element in the specified collection with the indicated weight
	 * value.
	 * 
	 * @param aWeight
	 *            The weight value to use for each element added.
	 * @param c
	 *            The elements to add to the WeightedCollection
	 * @return <tt>true</tt> if the WeightedCollection is changed by this
	 *         call.
	 * 
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(final Collection<? extends E> c, final int aWeight)
	{
		boolean modified = false;
		for (E item : c)
		{
			modified |= add(item, aWeight);
		}
		return modified;
	}

	/**
	 * This class is a simple wrapper to associate an object from a
	 * <tt>WeightedList</tt> and its weight.
	 * 
	 * @author boomer70
	 * 
	 * @param <T>
	 */
	static class WeightedItem<T>
	{
		private final T theElement;

		private int theWeight;

		/**
		 * This constructor creates a new <tt>WeightedItem</tt> with the
		 * specified weight.
		 * 
		 * @param element
		 *            The object this Item represents.
		 * @param weight
		 *            The weight of the item within the list.
		 */
		public WeightedItem(final T element, final int weight)
		{
			theElement = element;
			theWeight = weight;
		}

		/**
		 * Gets the wrapped object.
		 * 
		 * @return The object this item wraps
		 */
		public final T getElement()
		{
			return theElement;
		}

		/**
		 * Gets the weight of this object.
		 * 
		 * @return The weight of this item
		 */
		public final int getWeight()
		{
			return theWeight;
		}

		/**
		 * Adds the specified amount of weight to the item.
		 * 
		 * @param aWeight
		 *            an amount of weight to add.
		 */
		public void addWeight(final int aWeight)
		{
			theWeight += aWeight;
		}

		@Override
		public int hashCode()
		{
			return theWeight * 29
				+ (theElement == null ? 0 : theElement.hashCode());
		}

		/**
		 * Equals method. Note this is required in order to have the .equals()
		 * at the WeightedCollection level work properly (it is a deep equals)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o)
		{
			if (o == null)
			{
				return false;
			}
			else if (o == this)
			{
				return true;
			}
			else if (o instanceof WeightedItem)
			{
				WeightedItem<?> wi = (WeightedItem) o;
				return theWeight == wi.theWeight
					&& (theElement == null && wi.theElement == null || theElement != null
						&& theElement.equals(wi.theElement));
			}
			return false;
		}

		@Override
		public String toString()
		{
			return theElement + " (" + theWeight + ")";
		}
	}

	class WeightedIterator implements Iterator<E>
	{

		private final Iterator<WeightedItem<E>> iter = theData.iterator();

		private WeightedItem<E> currentEntry;

		private int currentReturned = 0;

		public boolean hasNext()
		{
			if (currentEntry == null)
			{
				if (!iter.hasNext())
				{
					return false;
				}
				currentEntry = iter.next();
				currentReturned = 0;
			}
			if (currentReturned < currentEntry.theWeight)
			{
				return true;
			}
			return iter.hasNext();
		}

		public E next()
		{
			if (currentEntry == null
				|| currentReturned >= currentEntry.theWeight)
			{
				currentEntry = iter.next();
				currentReturned = 0;
			}
			currentReturned++;
			return currentEntry.theElement;
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

	private class UnweightedIterator implements Iterator<E>
	{
		/** An iterator that iterates over the raw data elements. */
		private final Iterator<WeightedItem<E>> realIterator =
				theData.iterator();

		/**
		 * Checks if there are any more elements in the iteration.
		 * 
		 * @return <tt>true</tt> if there are more elements.
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return realIterator.hasNext();
		}

		/**
		 * Returns the next element in the iteration.
		 * 
		 * @return The next element.
		 * 
		 * @see java.util.Iterator#next()
		 */
		public E next()
		{
			return realIterator.next().getElement();
		}

		/**
		 * Removes from the WeightedCollection the last element returned from
		 * the iteration.
		 * 
		 * @see java.util.Iterator#remove()
		 */
		public void remove()
		{
			realIterator.remove();
		}
	}

	private static class WeightedItemComparator<WICT> implements
			Comparator<WeightedItem<WICT>>
	{

		private final Comparator<? super WICT> delegateComparator;

		public WeightedItemComparator(Comparator<? super WICT> comp)
		{
			delegateComparator = comp;
		}

		public int compare(WeightedItem<WICT> arg0, WeightedItem<WICT> arg1)
		{
			return delegateComparator.compare(arg0.getElement(), arg1
				.getElement());
		}

	}
}
