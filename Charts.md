# Express Charts #

## Introduction ##

> A simple framework to build & display charts in a highly interactive & personalizable manner similar to iGoogle.

> [![](http://gwtexpress.googlepages.com/charts.gif)](http://gwtexpress.com/charts)

## Features ##

  * chart widget that displays the given data in five different views (Text Table, Bar, Line, Pie, & Pie 3D Charts)
  * oolbar buttons for minizing & closing (hiding)
  * nD enabled: Chart widgets can be re-arranged according to user's preference

## Details ##

> Chart widget can be created simply by passing the data object that implements the below GraphData interface & the default view in the constructor

```
public interface GraphData {

    public String[] getXAxisLabels();

    public double[] getData();

    public String getChartTitle();
}
```
```
        ChartGroup cg = new ChartGroup(new GraphData() {
                    public String[] getXAxisLabels() {
                        return new String[] { "Entered but not booked", 
                                              "Booked but not Scheduled (Allocated)", 
                                              "Scheduled (Allocated) but not pick released", 
                                              "Pick released but not ship confirmed", 
                                              "Shipped in shipping but awaiting shipping in OM", 
                                              "Ship confirmed but not invoice interfaced" };
                    }

                    public double[] getData() {
                        return new double[] { 4534, 7437, 3445, 5945, 6032, 
                                              4687 };
                    }

                    public String getChartTitle() {
                        return "Sales Orders at a Glance";
                    }
                }, ChartImage.TYPE_BAR_CHART);
```

Check the [demo](http://gwtexpress.com/charts) to see these charts in action...

**Please provide your valuable feedback**