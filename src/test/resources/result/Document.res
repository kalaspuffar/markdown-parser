<h1>tensorflow-data</h1>
<p>An small example how to use tensorflow data (tf.data)</p>
<p></p>
<h3>Usage</h3>
<p></p>
<p>In order to try this repository you clone it on your drive. You'll probably need 12-20Gb of disk because of the large amount of image data.</p>
<p></p>
<p>First of download the image [Dataset](https://www.microsoft.com/en-us/download/details.aspx?id=54765) and put the picture in a structure where you have</p>
<code>
./PetImages/Cat/*.jpg
./PetImages/Dog/*.jpg
</code>
<p></p>
<p></p>
<p>I've not pin-point everything required to run this test scripts because I had most of it installed already.</p>
<p>But you need to install tensorflow, opencv2 and numpy atleast.</p>
<code>
pip install tensorflow opencv2-python numpy
</code>
<p></p>
<p>Then you run the create_dataset.py in order to create the train, test and validation tfrecords.</p>
<code>
python create_dataset.py
</code>
<p></p>
<p>Lastly you can train your model using the training script.</p>
<code>
python train.py
</code>
<p></p>
<p>If you have any question or suggestion the just reach out. Open an issue and I'll look into it.</p>
<p></p>
<p></p>
<h2>Tensorflow lite model.</h2>
<p></p>
<p>In order to use this model on a tensorflow lite enabled device you need to freeze your model using this command</p>
<p></p>
<code>
freeze_graph \
  --input_graph=./model2/graph.pbtxt \
  --input_checkpoint=./model2/model.ckpt-81852 \
  --input_binary=false \
  --output_graph=/tmp/frozen.pb \
  --output_node_names=input_tensor,output_pred
</code>
<p></p>
<p>In order to convert your model you need a tool called toco (Tensorflow Lite Optimizing Converter). Use the command below to build this tool in your tensorflow directory.</p>
<code>
bazel build //tensorflow/contrib/lite/toco:toco
</code>
<p></p>
<p>After that you convert it into a tensorflow lite model using the command below inside of your tensorflow directory.</p>
<p></p>
<code>
 ./bazel-bin/tensorflow/contrib/lite/toco/toco
   --input_file=/tmp/frozen.pb
   --input_format=TENSORFLOW_GRAPHDEF
   --output_format=TFLITE
   --output_file=/tmp/cat_vs_dogs.tflite
   --input_arrays=input_tensor
   --output_arrays=output_pred
   --input_shapes=1,224,224,3
</code>
<p></p>
<p>Modifying the demo provided by google you can then test your inference on your device.</p>