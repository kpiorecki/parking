package com.kpiorecki.parking.ejb.util;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.dozer.Mapper;

@Stateless
public class CollectionMapper {

	@Inject
	private Mapper mapper;

	public <S, D> ArrayList<D> mapToArrayList(Collection<S> source, Class<D> destinationClass) {
		ArrayList<D> arrayList = new ArrayList<D>(source.size());
		map(source, arrayList, destinationClass);

		return arrayList;
	}

	public <S, D> void map(Collection<S> source, Collection<D> destination, Class<D> destinationClass) {
		for (S element : source) {
			D mappedObject = mapper.map(element, destinationClass);
			destination.add(mappedObject);
		}
	}

}
