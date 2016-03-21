package org.kidneyomics.referenceseq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KmerHashMap<T> implements Map<KmerKey,Set<T>> {

	private final int kmerSize;
	private final int size;
	private final ArrayList<Set<T>> map;
	
	public KmerHashMap(int kmerSize) {
		this.kmerSize = kmerSize;
		this.size = (int) Math.round( Math.pow(5, kmerSize) );
		this.map = new ArrayList<Set<T>>(size);
		
		for(int i = 0; i < size; i++) {
			map.add(null);
		}
	}
	
	@Override
	public int size() {
		return this.size;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		
		if(key instanceof KmerKey) {
			
			KmerKey cast = (KmerKey) key;
			
			if(cast.getSize() != kmerSize) {
				throw new IllegalStateException("Incorrect kmer size");
			}
			
			if(cast.hashCode() > size) {
				throw new IllegalStateException("Illegal hashcode");
			}
			
			return true;
			
			
		} else {
			return false;
		}
		
	}
	
	public boolean containsHashCode(int hashCode) {
		if(hashCode < size) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<T> get(Object key) {
		
		if(key instanceof KmerKey) {
			
			KmerKey cast = (KmerKey) key;
			
			if(cast.getSize() != kmerSize) {
				throw new IllegalStateException("Incorrect kmer size");
			}
			
			
			Set<T> res = this.map.get(cast.hashCode());
			

			//lazily create hashsets
			if(res == null) {
				res = new HashSet<T>();
				this.map.add(cast.hashCode(), res);
			}
			
			return res;
			
		} else {
			return null;
		}
		
	}
	
	public Set<T> getByHashCode(int hashCode) {
		if(hashCode < size) {
			Set<T> res = this.map.get(hashCode);
			

			//lazily create hashsets
			if(res == null) {
				res = new HashSet<T>();
				this.map.add(hashCode, res);
			}
			
			return res;
		} else {
			return null;
		}
	}
	
	public T putSingle(KmerKey key, T chrPos) {

			

			
		if(key.getSize() != kmerSize) {
			throw new IllegalStateException("Incorrect kmer size");
		}
		
		this.getByHashCode(key.hashCode()).add(chrPos);
		
		return chrPos;

	}
	

	@Override
	public Set<T> put(KmerKey key, Set<T> value) {

		if(key.getSize() != kmerSize) {
			throw new IllegalStateException("Incorrect kmer size");
		}
		
		this.map.add(key.hashCode(), value);
		return value;

	}

	@Override
	public Set<T> remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends KmerKey, ? extends Set<T>> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<KmerKey> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Set<T>> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<java.util.Map.Entry<KmerKey, Set<T>>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}



}
