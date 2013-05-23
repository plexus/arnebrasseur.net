task :build do
  `middleman build`
end

task :deploy => :build do
  puts `scp -r build/* build/.htaccess arnebrasseur@arnebrasseur.net:subdomains/devblog`
end
