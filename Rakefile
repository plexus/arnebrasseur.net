task :build do
  `middleman build`
end

task :deploy do
  `scp -r build/* arnebrasseur@arnebrasseur.net:subdomains/www`
end
