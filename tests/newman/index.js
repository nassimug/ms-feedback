const newman = require('newman');
const dataPath = argv.data || './dataset.json';
});
  console.log('Newman run completed successfully');
  }
    process.exit(3);
    console.error('Failures:', failures);
  if (failures) {
  const failures = summary.run.failures && summary.run.failures.length;
  }
    process.exit(2);
    console.error('Newman run failed:', err);
  if (err) {
newman.run(opts, function (err, summary) {

console.log('Running newman with', opts);

if (fs.existsSync(dataPath)) opts.iterationData = dataPath;
if (fs.existsSync(envPath)) opts.env = require(envPath);

};
  reporters: 'cli'
  collection: require(collectionPath),
const opts = {

}
  process.exit(1);
  console.error('Collection not found:', collectionPath);
if (!fs.existsSync(collectionPath)) {
const envPath = argv.env || './env.json';
const collectionPath = argv.collection || './collection.json';

const argv = require('minimist')(process.argv.slice(2));
const fs = require('fs');

