package com.github.kettoleon.primordial.soup.model.genetics;


import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GeneReaderTest {

    @Test
    public void readInt_returns0_between0and0() {

        //WHEN
        int actual = new GeneReader(new double[]{0}).nextInt(0, 0);

        //THEN
        assertThat(actual, equalTo(0));

    }

    @Test
    public void readInt_returns0_between0and1_whenValueIs1() {

        //WHEN
        int actual = new GeneReader(new double[]{1}).nextInt(0, 1);

        //THEN
        assertThat(actual, equalTo(0));

    }


    @Test
    public void readInt_returns0_between0and1_whenValueIs0() {

        //WHEN
        int actual = new GeneReader(new double[]{0}).nextInt(0, 1);

        //THEN
        assertThat(actual, equalTo(0));

    }

    @Test
    public void readInt_returns9_between0and10_whenValueIs1() {

        //WHEN
        int actual = new GeneReader(new double[]{1}).nextInt(0, 10);

        //THEN
        assertThat(actual, equalTo(9));

    }

    @Test
    public void readInt_returns9_between0and10_whenValueIsNear1() {

        //WHEN
        int actual = new GeneReader(new double[]{0.999999999f}).nextInt(0, 10);

        //THEN
        assertThat(actual, equalTo(9));

    }

}