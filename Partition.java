private List<List<String>> getPartitionedList(List<String> origList, int partitionSize) {
    List<List<String>> partitions = new ArrayList<>();
    for (int i = 0; i < origList.size(); i += partitionSize) {
        partitions.add(origList.subList(i, Math.min(i + partitionSize, origList.size())));
    }
    return partitions;
}
