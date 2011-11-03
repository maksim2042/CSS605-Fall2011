$header = nil
def process_averages(file)
  lines = open(file).readlines
  $header = lines.shift
  results = (0..7).collect{[]}
  lines.each{ |line| line.split(',').each_with_index{|x,i| results[i] << x.to_f} }
  results.pop
  results.collect{ |vector| vector.inject{|s,i| s+i} / vector.size }
end

averages = %w{outputInfluence.csv outputTerritories.csv outputWealth.csv}.collect do |name|
  process_averages("outputFiles/#{name}")
end

puts $header.split(',').join("\t").chomp
averages.each do |row|
  puts row.join("\t")
end