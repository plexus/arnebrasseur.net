task :default => :deploy

desc 'Middleman build'
task :build do
  `middleman build`
end

desc 'Build and push out'
task :deploy => :build do
  puts `scp -r build/* build/.htaccess arnebrasseur@arnebrasseur.net:subdomains/devblog`
end
