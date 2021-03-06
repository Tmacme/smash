/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.berkeley.cs.amplab.calldiff;

import com.google.common.base.Throwables;

import edu.berkeley.cs.amplab.calldiff.Call;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A helper class for the unit tests that allows for creating {@link Call} instances, from either
 * explicitly provided data, or randomly based on the information in {@link TestReference}.
 */
public class TestCall implements Call {

  public static TestCall create(
      String contig,
      int position,
      String reference,
      List<String> alternates,
      List<Integer> genotype) {
    return create(
        contig,
        position,
        reference,
        alternates,
        genotype,
        Optional.empty());
  }

  public static TestCall create(
      String contig,
      int position,
      String reference,
      List<String> alternates,
      List<Integer> genotype,
      Optional<Phaseset> phaseset) {
    return new TestCall(
        contig,
        position,
        reference,
        alternates,
        genotype,
        phaseset);
  }

  public static TestCall create(
      String contig,
      int position,
      String reference,
      List<String> alternates,
      List<Integer> genotype,
      Phaseset phaseset) {
    return create(
        contig,
        position,
        reference,
        alternates,
        genotype,
        Optional.of(phaseset));
  }

  public static ArrayList<Call> randomCalls(
      Random random,
      String contig,
      int contigLength,
      int maxCallLength,
      int numberOfCalls) {
    return randomCalls(
        random,
        contig,
        contigLength,
        maxCallLength,
        numberOfCalls,
        () -> Optional.empty());
  }

  public static ArrayList<Call> randomCalls(
      Random random,
      String contig,
      int contigLength,
      int maxCallLength,
      int numberOfCalls,
      Supplier<Optional<Phaseset>> phaseset) {
    try {
      Set<Call> calls = new HashSet<>();
      for (int i = 0; i < numberOfCalls; ++i) {
        int length = 1 == maxCallLength ? 1 : random.nextInt(maxCallLength - 1) + 1,
            max = contigLength - length,
            start = 0 == max ? 1 : random.nextInt(max);
        calls.add(create(
            contig,
            start,
            TestReference.reader().read(
                reference -> reference.get(contig, start - 1, start + length - 1)),
            Collections.emptyList(),
            Arrays.asList(0, 0),
            phaseset.get()));
      }
      ArrayList<Call> list = new ArrayList<>();
      list.addAll(calls);
      Collections.sort(list, Comparator.comparing(Call::position));
      return list;
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  private final List<String> alternates;
  private final String contig;
  private final List<Integer> genotype;
  private final Optional<Phaseset> phaseset;
  private final int position;
  private final String reference;

  private TestCall(
      String contig,
      int position,
      String reference,
      List<String> alternates,
      List<Integer> genotype,
      Optional<Phaseset> phaseset) {
    this.contig = contig;
    this.position = position;
    this.reference = reference;
    this.alternates = alternates;
    this.genotype = genotype;
    this.phaseset = phaseset;
  }

  @Override
  public List<String> alternates() {
    return alternates;
  }

  @Override
  public String contig() {
    return contig;
  }

  @Override
  public boolean equals(Object obj) {
    return HASH_CODE_AND_EQUALS.equals(this, obj);
  }

  @Override
  public List<Integer> genotype() {
    return genotype;
  }

  @Override
  public int hashCode() {
    return HASH_CODE_AND_EQUALS.hashCode(this);
  }

  @Override
  public Optional<Phaseset> phaseset() {
    return phaseset;
  }

  @Override
  public int position() {
    return position;
  }

  @Override
  public String reference() {
    return reference;
  }

  @Override
  public String toString() {
    return TO_STRING.apply(this);
  }
}
